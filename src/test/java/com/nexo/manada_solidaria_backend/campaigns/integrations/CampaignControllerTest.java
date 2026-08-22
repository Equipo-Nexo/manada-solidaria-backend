package com.nexo.manada_solidaria_backend.campaigns.integrations;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationFundraisingCampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.models.*;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils.NEWS_UPDATE_END_DATE;
import static com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils.NEWS_UPDATE_START_DATE;
import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class CampaignControllerTest extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("POST /campaigns — Códigos de estado por payload")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideCreateCases")
    void createCampaignTests(String testName, CreateCampaignRequest request, HttpStatus expectedStatus, String expectedType) throws Exception {
        var result = mockMvc.perform(post("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().is(expectedStatus.value()));

        if (expectedStatus.is2xxSuccessful()) {
            result.andExpect(jsonPath("$.type").value(expectedType))
                    .andExpect(jsonPath("$.status").value("CREATED"));
        }
    }

    @Test
    @DisplayName("POST /campaigns válido: persiste, refleja inputs y asocia Owner por JWT")
    void create_persistsCampaignWithAuthenticatedOwnerAndData() throws Exception {
        UUID adminId = userRepository.findByUsername("admin").orElseThrow().getId();

        String responseBody = mockMvc.perform(post("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(MockCampaignDataUtils.FUNDRAISING_VALID_FULL)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("fundraising"))
                .andExpect(jsonPath("$.title").value("Operación de mofli"))
                .andExpect(jsonPath("$.ownerId").value(adminId.toString()))
                .andReturn().getResponse().getContentAsString();

        UUID generatedId = UUID.fromString(mapper.readTree(responseBody).get("id").asText());
        Campaign savedCampaign = campaignRepository.findById(generatedId).orElseThrow();

        assertThat(savedCampaign.getTitle()).isEqualTo("Operación de mofli");
        assertThat(savedCampaign.getOwner().getId()).isEqualTo(adminId);

        FundraisingCampaign savedFundraising = (FundraisingCampaign) savedCampaign;
        assertThat(savedFundraising.getAmountToBeCollected()).isEqualTo(150000L);
        assertThat(savedFundraising.getAccountAlias()).isEqualTo("recaudacion.mofli");
    }

    @Test
    @DisplayName("POST /campaigns sin token devuelve 401")
    void create_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(MockCampaignDataUtils.NEWS_VALID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /campaigns con token inválido devuelve 401")
    void create_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/campaigns")
                        .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(MockCampaignDataUtils.NEWS_VALID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /campaigns devuelve News y Donation, excluyendo Fundraising")
    void getCampaigns_returnsNewsAndDonation() throws Exception {

        var owner = userRepository.findByUsername("admin").orElseThrow();

        campaignRepository.save(MockCampaignDataUtils.buildDonationModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildNewsModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildFundraisingModel(owner));

        String response = mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).doesNotContain("\"type\":\"fundraising\"");
    }

    @Test
    @DisplayName("GET /campaigns?category=VACCINATION devuelve solo noticias de vacunación")
    void getCampaigns_filterVaccination() throws Exception {

        var owner = userRepository.findByUsername("admin").orElseThrow();

        campaignRepository.save(MockCampaignDataUtils.buildDonationModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildNewsModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildFundraisingModel(owner));

        mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("category", "VACCINATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].type").value("vaccination"));
    }

    @Test
    @DisplayName("GET /campaigns?category=DONATION devuelve solo campañas de donación")
    void getCampaigns_filterDonation() throws Exception {

        var owner = userRepository.findByUsername("admin").orElseThrow();

        campaignRepository.save(MockCampaignDataUtils.buildDonationModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildNewsModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildFundraisingModel(owner));

        mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("category", "DONATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].type").value("donation"));
    }

    @Test
    @DisplayName("GET /fundraising_campaigns devuelve solo campañas de recaudación")
    void getFundraisingCampaigns_returnsOnlyFundraisings() throws Exception {

        var owner = userRepository.findByUsername("admin").orElseThrow();

        campaignRepository.save(MockCampaignDataUtils.buildDonationModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildNewsModel(owner));
        campaignRepository.save(MockCampaignDataUtils.buildFundraisingModel(owner));

        mockMvc.perform(get("/campaigns/fundraising_campaigns")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].type").value("fundraising"));
    }

    @Test
    @DisplayName("GET /campaigns?category=HOLA devuelve 400")
    void getCampaigns_invalidCategory_returnsBadRequest() throws Exception {

        mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("category", "HOLA"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /campaigns/{id} del owner: 204 y la campaña deja de existir")
    void delete_asOwner_removesCampaign() throws Exception {
        UUID campaignId = campaignRepository.save(MockCampaignDataUtils.buildDonationModel(admin())).getId();

        mockMvc.perform(delete("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        assertThat(campaignRepository.findById(campaignId)).isEmpty();
    }

    @Test
    @DisplayName("DELETE /campaigns/{id} de un usuario que no es el owner devuelve 403 y no elimina")
    void delete_asNonOwner_returnsForbiddenAndKeepsCampaign() throws Exception {
        UUID campaignId = saveCampaignOwnedByOtherUser().getId();

        mockMvc.perform(delete("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());

        assertThat(campaignRepository.findById(campaignId)).isPresent();
    }

    @DisplayName("DELETE /campaigns/{id} de una donación en estado final devuelve 409 y no elimina")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideFinalDonationStatuses")
    void delete_donationInFinalState_returnsConflictAndKeepsCampaign(String testName, DonationFundraisingCampaignStatus status) throws Exception {
        UUID campaignId = saveDonationWithStatus(admin(), status).getId();

        mockMvc.perform(delete("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict())
                // Substring ASCII: robusto ante la normalización de tildes del repo al commitear.
                .andExpect(jsonPath("$.errors", hasItem(containsString("finalizada"))));

        assertThat(campaignRepository.findById(campaignId)).isPresent();
    }

    @Test
    @DisplayName("DELETE /campaigns/{id} de una noticia en estado FINISHED devuelve 409 y no elimina")
    void delete_finishedNewsCampaign_returnsConflictAndKeepsCampaign() throws Exception {
        NewsCampaign campaign = MockCampaignDataUtils.buildNewsModel(admin());
        campaign.setStatusHistory(new ArrayList<>(List.of(new NewsCampaignStatusHistory(NewsCampaignStatus.FINISHED, campaign))));
        UUID campaignId = campaignRepository.save(campaign).getId();

        mockMvc.perform(delete("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict());

        assertThat(campaignRepository.findById(campaignId)).isPresent();
    }

    @Test
    @DisplayName("DELETE /campaigns/{id} inexistente devuelve 404 con mensaje")
    void delete_nonExistentCampaign_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/campaigns/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", hasItem(containsString("no existe"))));
    }

    @Test
    @DisplayName("DELETE /campaigns/{id} sin token devuelve 401")
    void delete_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/campaigns/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /campaigns/{id} con token inválido devuelve 401")
    void delete_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/campaigns/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /campaigns/{id} del owner actualiza una donación correctamente")
    void update_donation_asOwner_updatesCampaign() throws Exception {
        DonationCampaign campaign = MockCampaignDataUtils.buildDonationModel(admin());
        UUID campaignId = campaignRepository.save(campaign).getId();

        mockMvc.perform(put("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(MockCampaignDataUtils.buildDonationUpdateRequest())))
                .andExpect(status().isNoContent());

        DonationCampaign updated = (DonationCampaign) campaignRepository.findById(campaignId).orElseThrow();

        assertThat(updated.getTitle()).isEqualTo("Título Donación Editado");
        assertThat(updated.getDescription()).isEqualTo("Descripción editada");
        assertThat(updated.getPhoneNumber().areaCode()).isEqualTo("3533");
        assertThat(updated.getPhoneNumber().number()).isEqualTo("436249");
        assertThat(updated.getCampaignEndDate()).isEqualTo(LocalDate.now().plusMonths(2));
    }

    @Test
    @DisplayName("PUT /campaigns/{id} actualiza campos propios de fundraising")
    void update_fundraising_updatesSpecificFields() throws Exception {
        FundraisingCampaign campaign = MockCampaignDataUtils.buildFundraisingModel(admin());
        UUID campaignId = campaignRepository.save(campaign).getId();

        mockMvc.perform(put("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildFundraisingUpdateRequest()
                        )))
                .andExpect(status().isNoContent());

        FundraisingCampaign updated = (FundraisingCampaign) campaignRepository.findById(campaignId).orElseThrow();

        assertThat(updated.getAccountAlias()).isEqualTo("nuevo.alias");
        assertThat(updated.getAmountToBeCollected()).isEqualTo(100000L);
        assertThat(updated.getAmountCollected()).isEqualTo(25000L);
        assertThat(updated.getCampaignEndDate()).isEqualTo(LocalDate.now().plusMonths(3));
    }

    @Test
    @DisplayName("PUT /campaigns/{id} actualiza campos propios de NEWS")
    void update_news_updatesSpecificFields() throws Exception {
        NewsCampaign campaign = MockCampaignDataUtils.buildNewsModel(admin());
        UUID campaignId = campaignRepository.save(campaign).getId();

        mockMvc.perform(put("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildNewsUpdateRequest()
                        )))
                .andExpect(status().isNoContent());

        NewsCampaign updated = (NewsCampaign) campaignRepository.findById(campaignId).orElseThrow();

        assertThat(updated.getCategory()).isEqualTo(NewsCampaignCategory.OTHER);
        assertThat(updated.getNewsStartDateTime()).isEqualTo(NEWS_UPDATE_START_DATE);
        assertThat(updated.getNewsEndDateTime()).isEqualTo(NEWS_UPDATE_END_DATE);
    }

    @Test
    @DisplayName("PUT /campaigns/{id} de otro usuario devuelve 403")
    void update_asNonOwner_returnsForbidden() throws Exception {
        UUID campaignId = saveCampaignOwnedByOtherUser().getId();

        mockMvc.perform(put("/campaigns/" + campaignId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildDonationUpdateRequest()
                        )))
                .andExpect(status().isForbidden());

        DonationCampaign campaign = (DonationCampaign) campaignRepository.findById(campaignId).orElseThrow();

        assertThat(campaign.getTitle()).isEqualTo("Título Donación Test");
    }

    @Test
    @DisplayName("PUT /campaigns/{id} inexistente devuelve 404")
    void update_nonExistentCampaign_returnsNotFound() throws Exception {

        mockMvc.perform(put("/campaigns/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildDonationUpdateRequest()
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors",
                        hasItem(containsString("no existe"))));
    }

    @Test
    @DisplayName("PUT /campaigns/{id} sin token devuelve 401")
    void update_withoutToken_returnsUnauthorized() throws Exception {

        mockMvc.perform(put("/campaigns/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildDonationUpdateRequest()
                        )))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /campaigns/{id} con token inválido devuelve 401")
    void update_withInvalidToken_returnsUnauthorized() throws Exception {

        mockMvc.perform(put("/campaigns/" + UUID.randomUUID())
                        .header("Authorization",
                                "Bearer " + INVALID_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildDonationUpdateRequest()
                        )))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /campaigns/{id} actualiza recaudación quitando la ubicación exitosamente")
    void update_fundraising_withoutLocation_success() throws Exception {
        FundraisingCampaign campaign = campaignRepository.save(MockCampaignDataUtils.buildFundraisingModel(admin()));

        mockMvc.perform(put("/campaigns/" + campaign.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildFundraisingUpdateRequestWithoutLocation()
                        )))
                .andExpect(status().isNoContent());

        FundraisingCampaign updated = (FundraisingCampaign) campaignRepository.findById(campaign.getId()).orElseThrow();
        assertThat(updated.getLocation()).isNull();
    }

    @Test
    @DisplayName("PUT /campaigns/{id} en noticia sin ubicación falla con 400 Bad Request")
    void update_news_withoutLocation_returnsBadRequest() throws Exception {
        NewsCampaign campaign = campaignRepository.save(MockCampaignDataUtils.buildNewsModel(admin()));

        mockMvc.perform(put("/campaigns/" + campaign.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(
                                MockCampaignDataUtils.buildNewsUpdateRequestWithoutLocation()
                        )))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideGetCampaignCases")
    @Sql(
            scripts="/sql/campaigns/get-campaigns.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getCampaign(String testName, UUID id, String expectedType, String expectedTitle, Integer expectedItems) throws Exception {

        ResultActions result = mockMvc.perform(
                get("/campaigns/" + id)
                        .header("Authorization", "Bearer " + accessToken)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.type").value(expectedType))
                .andExpect(jsonPath("$.title").value(expectedTitle));

        if(expectedItems != null){
            result.andExpect(jsonPath("$.items.length()").value(expectedItems));
        }
    }

    @Test
    @DisplayName("GET /campaigns/{id} inexistente devuelve 404")
    void getCampaign_nonExisting_returnsNotFound() throws Exception {

        mockMvc.perform(get("/campaigns/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors",
                        hasItem(containsString("no existe"))));
    }

    @Test
    @DisplayName("GET /campaigns/{id} sin token devuelve 401")
    void getCampaign_withoutToken_returnsUnauthorized() throws Exception {

        mockMvc.perform(get("/campaigns/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /campaigns/{id} con token inválido devuelve 401")
    void getCampaign_withInvalidToken_returnsUnauthorized() throws Exception {

        mockMvc.perform(get("/campaigns/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideValidStatusTransitions")
    @DisplayName("PATCH /campaigns/{id}/status permite transiciones válidas")
    void transitionStatus_validTransition_returnsOk(
            String testName,
            CampaignType campaignType,
            Enum<?> currentStatus,
            Enum<?> targetStatus
    ) throws Exception {

        Campaign<?, ?> campaign = saveCampaignWithStatus(
                campaignType,
                currentStatus.name()
        );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + campaign.getId() + "/status")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(
                                        new com.nexo.manada_solidaria_backend.campaigns.controllers.requests.TransitionCampaignStatusRequest(
                                                targetStatus.name()
                                        )
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(targetStatus.name()));

        Campaign<?, ?> updated =
                campaignRepository.findById(campaign.getId()).orElseThrow();

        assertThat(updated.getCurrentStatus().getStatus())
                .isEqualTo(targetStatus);
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideInvalidStatusTransitions")
    @DisplayName("PATCH /campaigns/{id}/status rechaza transiciones inválidas")
    void transitionStatus_invalidTransition_returnsConflict(
            String testName,
            CampaignType campaignType,
            Enum<?> currentStatus,
            Enum<?> targetStatus
    ) throws Exception {

        Campaign<?, ?> campaign = saveCampaignWithStatus(
                campaignType,
                currentStatus.name()
        );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + campaign.getId() + "/status")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(
                                        new com.nexo.manada_solidaria_backend.campaigns.controllers.requests.TransitionCampaignStatusRequest(
                                                targetStatus.name()
                                        )
                                ))
                )
                .andExpect(status().isConflict());

        Campaign<?, ?> unchanged =
                campaignRepository.findById(campaign.getId()).orElseThrow();

        assertThat(unchanged.getCurrentStatus().getStatus())
                .isEqualTo(currentStatus);
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideNonOwnerTransitions")
    @DisplayName("PATCH /campaigns/{id}/status de otro usuario devuelve 403")
    void transitionStatus_asNonOwner_returnsForbidden(
            String testName,
            CampaignType campaignType,
            String currentStatus,
            String targetStatus
    ) throws Exception {

        User other = new User(
                "otro-status-user-" + UUID.randomUUID(),
                "x",
                new Profile(
                        "otro-status-" + UUID.randomUUID() + "@mail.com",
                        new PhoneNumber("353", "4014524"),
                        List.of(Rol.COMMUNITY)
                )
        );

        userRepository.save(other);

        Campaign<?, ?> campaign =
                saveCampaignWithStatusOwnedByUser(
                        campaignType,
                        currentStatus,
                        other
                );

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + campaign.getId() + "/status")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(
                                        new com.nexo.manada_solidaria_backend.campaigns.controllers.requests.TransitionCampaignStatusRequest(
                                                targetStatus
                                        )
                                ))
                )
                .andExpect(status().isForbidden());

        Campaign<?, ?> unchanged =
                campaignRepository.findById(campaign.getId()).orElseThrow();

        assertThat(unchanged.getCurrentStatus().getStatus().name())
                .isEqualTo(currentStatus);
    }

    @Test
    @DisplayName("PATCH /campaigns/{id}/status de campaña inexistente devuelve 404")
    void transitionStatus_nonExistingCampaign_returnsNotFound() throws Exception {

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + UUID.randomUUID() + "/status")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(
                                        new com.nexo.manada_solidaria_backend.campaigns.controllers.requests.TransitionCampaignStatusRequest(
                                                "FINISHED"
                                        )
                                ))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors",
                        hasItem(containsString("no existe"))));
    }

    @Test
    @DisplayName("PATCH /campaigns/{id}/status sin token devuelve 401")
    void transitionStatus_withoutToken_returnsUnauthorized() throws Exception {

        UUID campaignId =
                campaignRepository.save(
                        MockCampaignDataUtils.buildDonationModel(admin())
                ).getId();

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + campaignId + "/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(
                                        new com.nexo.manada_solidaria_backend.campaigns.controllers.requests.TransitionCampaignStatusRequest(
                                                "COMPLETED"
                                        )
                                ))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /campaigns/{id}/status con token inválido devuelve 401")
    void transitionStatus_withInvalidToken_returnsUnauthorized() throws Exception {

        UUID campaignId =
                campaignRepository.save(
                        MockCampaignDataUtils.buildDonationModel(admin())
                ).getId();

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + campaignId + "/status")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(
                                        new com.nexo.manada_solidaria_backend.campaigns.controllers.requests.TransitionCampaignStatusRequest(
                                                "COMPLETED"
                                        )
                                ))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /campaigns/{id}/status con estado vacío devuelve 400")
    void transitionStatus_blankStatus_returnsBadRequest() throws Exception {

        UUID campaignId =
                campaignRepository.save(
                        MockCampaignDataUtils.buildDonationModel(admin())
                ).getId();

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + campaignId + "/status")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "status": ""
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /campaigns/{id}/status con estado inexistente devuelve 400")
    void transitionStatus_invalidStatus_returnsBadRequest() throws Exception {

        UUID campaignId =
                campaignRepository.save(
                        MockCampaignDataUtils.buildDonationModel(admin())
                ).getId();

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/campaigns/" + campaignId + "/status")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "status": "INVALID_STATUS"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());
    }

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }

    private Campaign saveCampaignOwnedByOtherUser() {
        User other = new User("otro-campaign-user", "x", new Profile("otro@mail.com", new PhoneNumber("3533", "436249"), List.of(Rol.COMMUNITY)));
        userRepository.save(other);
        return campaignRepository.save(MockCampaignDataUtils.buildDonationModel(other));
    }

    private Campaign saveDonationWithStatus(User owner, DonationFundraisingCampaignStatus status) {
        DonationCampaign campaign = MockCampaignDataUtils.buildDonationModel(owner);
        // Fila con finishedAt == null: es el estado vigente que resuelve getCurrentStatus().
        campaign.setStatusHistory(new ArrayList<>(List.of(new DonationCampaignStatusHistory(status, campaign))));
        return campaignRepository.save(campaign);
    }

    private Campaign<?, ?> saveCampaignWithStatus(CampaignType type, String status) {
        User owner = admin();

        return switch (type) {
            case DONATION -> {
                DonationCampaign campaign =
                        MockCampaignDataUtils.buildDonationModel(owner);

                DonationFundraisingCampaignStatus campaignStatus =
                        DonationFundraisingCampaignStatus.valueOf(status);

                campaign.setStatusHistory(new ArrayList<>(
                        List.of(
                                new DonationCampaignStatusHistory(
                                        campaignStatus,
                                        campaign
                                )
                        )
                ));

                yield campaignRepository.save(campaign);
            }

            case FUNDRAISING -> {
                FundraisingCampaign campaign =
                        MockCampaignDataUtils.buildFundraisingModel(owner);

                DonationFundraisingCampaignStatus campaignStatus =
                        DonationFundraisingCampaignStatus.valueOf(status);

                campaign.setStatusHistory(new ArrayList<>(
                        List.of(
                                new FundraisingCampaignStatusHistory(
                                        campaignStatus,
                                        campaign
                                )
                        )
                ));

                yield campaignRepository.save(campaign);
            }

            case NEWS -> {
                NewsCampaign campaign = MockCampaignDataUtils.buildNewsModel(owner);
                NewsCampaignStatus campaignStatus = NewsCampaignStatus.valueOf(status);

                campaign.setStatusHistory(new ArrayList<>(
                        List.of(
                                new NewsCampaignStatusHistory(
                                        campaignStatus,
                                        campaign
                                )
                        )
                ));

                yield campaignRepository.save(campaign);
            }
        };
    }

    private Campaign<?, ?> saveCampaignWithStatusOwnedByUser(
            CampaignType type,
            String status,
            User owner
    ) {
        return switch (type) {
            case DONATION -> {
                DonationCampaign campaign =
                        MockCampaignDataUtils.buildDonationModel(owner);

                campaign.setStatusHistory(new ArrayList<>(List.of(
                        new DonationCampaignStatusHistory(
                                DonationFundraisingCampaignStatus.valueOf(status),
                                campaign
                        )
                )));

                yield campaignRepository.save(campaign);
            }

            case FUNDRAISING -> {
                FundraisingCampaign campaign =
                        MockCampaignDataUtils.buildFundraisingModel(owner);

                campaign.setStatusHistory(new ArrayList<>(List.of(
                        new FundraisingCampaignStatusHistory(
                                DonationFundraisingCampaignStatus.valueOf(status),
                                campaign
                        )
                )));

                yield campaignRepository.save(campaign);
            }

            case NEWS -> {
                NewsCampaign campaign =
                        MockCampaignDataUtils.buildNewsModel(owner);

                campaign.setStatusHistory(new ArrayList<>(List.of(
                        new NewsCampaignStatusHistory(
                                NewsCampaignStatus.valueOf(status),
                                campaign
                        )
                )));

                yield campaignRepository.save(campaign);
            }
        };
    }
}