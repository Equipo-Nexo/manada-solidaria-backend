package com.nexo.manada_solidaria_backend.campaigns.integrations;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationCampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaginStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationCampaignStatusHistory;
import com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaignStatusHistory;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                        .content(toJson(MockCampaignDataUtils.DONATION_VALID_FULL)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("DONATION"))
                .andExpect(jsonPath("$.title").value("Operación de mofli"))
                .andExpect(jsonPath("$.ownerId").value(adminId.toString()))
                .andReturn().getResponse().getContentAsString();

        UUID generatedId = UUID.fromString(mapper.readTree(responseBody).get("id").asText());
        Campaign savedCampaign = campaignRepository.findById(generatedId).orElseThrow();

        assertThat(savedCampaign.getTitle()).isEqualTo("Operación de mofli");
        assertThat(savedCampaign.getOwner().getId()).isEqualTo(adminId);

        DonationCampaign savedDonation = (DonationCampaign) savedCampaign;
        assertThat(savedDonation.getAmountToBeCollected()).isEqualTo(150000L);
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
    @DisplayName("GET /campaigns — Trae todas las campañas paginadas por defecto")
    void getAll_returnsAllCampaignsWithDefaultPagination() throws Exception {
        Campaign news = MockCampaignDataUtils.buildNewsModel(userRepository.findByUsername("admin").orElseThrow());
        Campaign donation = MockCampaignDataUtils.buildDonationModel(userRepository.findByUsername("admin").orElseThrow());
        campaignRepository.save(news);
        campaignRepository.save(donation);

        mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2)) // Valida que trajo ambas
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /campaigns?type=DONATION — Filtra trayendo solo donaciones")
    void getAll_withTypeDonation_returnsOnlyDonations() throws Exception {
        Campaign news = MockCampaignDataUtils.buildNewsModel(userRepository.findByUsername("admin").orElseThrow());
        Campaign donation = MockCampaignDataUtils.buildDonationModel(userRepository.findByUsername("admin").orElseThrow());
        campaignRepository.save(news);
        campaignRepository.save(donation);

        mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("type", "DONATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].type").value("DONATION"));
    }

    @Test
    @DisplayName("GET /campaigns?type=HOLA — Devuelve 400 Bad Request por tipo inválido")
    void getAll_withInvalidType_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("type", "HOLA"))
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
    void delete_donationInFinalState_returnsConflictAndKeepsCampaign(String testName, DonationCampaignStatus status) throws Exception {
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
        campaign.setStatusHistory(new ArrayList<>(List.of(new NewsCampaignStatusHistory(NewsCampaginStatus.FINISHED, campaign))));
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

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }

    private Campaign saveCampaignOwnedByOtherUser() {
        User other = new User("otro-campaign-user", "x", new Profile("otro@mail.com", "111", List.of(Rol.COMMUNITY)));
        userRepository.save(other);
        return campaignRepository.save(MockCampaignDataUtils.buildDonationModel(other));
    }

    private Campaign saveDonationWithStatus(User owner, DonationCampaignStatus status) {
        DonationCampaign campaign = MockCampaignDataUtils.buildDonationModel(owner);
        // Fila con finishedAt == null: es el estado vigente que resuelve getCurrentStatus().
        campaign.setStatusHistory(new ArrayList<>(List.of(new DonationCampaignStatusHistory(status, campaign))));
        return campaignRepository.save(campaign);
    }
}