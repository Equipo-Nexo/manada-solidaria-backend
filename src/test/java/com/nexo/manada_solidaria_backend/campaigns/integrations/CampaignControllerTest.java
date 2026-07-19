package com.nexo.manada_solidaria_backend.campaigns.integrations;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.FundraisingCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
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
                        .content(toJson(MockCampaignDataUtils.FUNDRAISING_VALID_FULL)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("FUNDRAISING"))
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

        assertThat(response).doesNotContain("\"type\":\"FUNDRAISING\"");
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
                .andExpect(jsonPath("$.content[0].type").value("NEWS"))
                .andExpect(jsonPath("$.content[0].newsCategory").value("VACCINATION"));
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
                .andExpect(jsonPath("$.content[0].type").value("DONATION"));
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
                .andExpect(jsonPath("$.content[0].type").value("FUNDRAISING"));
    }

    @Test
    @DisplayName("GET /campaigns?category=HOLA devuelve 400")
    void getCampaigns_invalidCategory_returnsBadRequest() throws Exception {

        mockMvc.perform(get("/campaigns")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("category", "HOLA"))
                .andExpect(status().isBadRequest());
    }
}