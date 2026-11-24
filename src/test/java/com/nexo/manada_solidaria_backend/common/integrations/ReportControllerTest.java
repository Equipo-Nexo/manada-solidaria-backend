package com.nexo.manada_solidaria_backend.common.integrations;

import com.nexo.manada_solidaria_backend.common.controllers.requests.CreateReportPostRequest;
import com.nexo.manada_solidaria_backend.common.data.enums.ReportableType;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.common.mail.ReportMailer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockReportDataUtils.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "app.report.threshold=1",
        "app.report.admin-email=admin@manadasolidaria.com",
        "app.report.frontend-url=https://paginamanadasolidaria.com"
})
class ReportControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.common.utils.MockReportDataUtils#";

    private static final String FRONTEND_URL = "https://paginamanadasolidaria.com";
    private static final String ADMIN_EMAIL = "admin@manadasolidaria.com";

    @MockitoBean
    private ReportMailer reportMailer;

    @Test
    @DisplayName("POST /reports registra el reporte exitosamente con status 201")
    @Sql("/sql/common/report-data.sql")
    void reportPost_success() throws Exception {
        mockMvc.perform(
                        post("/reports")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(VALID_ANIMAL_REPORT_REQUEST))
                )
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /reports al intentar reportar una publicación propia devuelve 400")
    @Sql("/sql/common/report-data.sql")
    void reportPost_ownPost_returnsBadRequest() throws Exception {
        String notAdminToken = getAccessTokenForUser("NOTADMIN");

        mockMvc.perform(
                        post("/reports")
                                .header("Authorization", "Bearer " + notAdminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(VALID_ANIMAL_REPORT_REQUEST))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem(containsString("propi"))));
    }

    @Test
    @DisplayName("POST /reports al reportar dos veces la misma publicación devuelve 409 Conflict")
    @Sql("/sql/common/report-data.sql")
    void reportPost_duplicateReport_returnsConflict() throws Exception {
        mockMvc.perform(
                post("/reports")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(VALID_ANIMAL_REPORT_REQUEST))
        ).andExpect(status().isCreated());

        mockMvc.perform(
                        post("/reports")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(VALID_ANIMAL_REPORT_REQUEST))
                )
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /reports con una publicación inexistente devuelve 404")
    void reportPost_nonExistentPost_returnsNotFound() throws Exception {
        CreateReportPostRequest invalidRequest = new CreateReportPostRequest(
                UUID.randomUUID(), ReportableType.ANIMAL_POST
        );

        mockMvc.perform(
                        post("/reports")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(invalidRequest))
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("POST /reports sin autenticación válida devuelve 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUnauthorizedReportCases")
    void reportPost_unauthorized(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = post("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(VALID_ANIMAL_REPORT_REQUEST));

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /reports en ANIMAL_POST al alcanzar el umbral envía el mail con /animal/detalle/{id}")
    @Sql("/sql/common/report-data.sql")
    void reportPost_animalPostReachesThreshold_sendsEmailWithAnimalUrl() throws Exception {
        mockMvc.perform(
                        post("/reports")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(VALID_ANIMAL_REPORT_REQUEST))
                )
                .andExpect(status().isCreated());

        String expectedUrl = FRONTEND_URL + "/animal/detalle/" + MOCK_POST_ID;
        verify(reportMailer, times(1)).send(
                eq(ADMIN_EMAIL),
                eq(new ReportMailer.ReportInformation(expectedUrl))
        );
    }

    @Test
    @DisplayName("POST /reports en campaña no colecta al alcanzar el umbral envía el mail con /campanias/{id}")
    @Sql("/sql/common/report-data.sql")
    void reportPost_donationCampaignReachesThreshold_sendsEmailWithCampaignsUrl() throws Exception {
        mockMvc.perform(
                        post("/reports")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(VALID_DONATION_CAMPAIGN_REPORT_REQUEST))
                )
                .andExpect(status().isCreated());

        String expectedUrl = FRONTEND_URL + "/campanias/" + MOCK_DONATION_CAMPAIGN_ID;
        verify(reportMailer, times(1)).send(
                eq(ADMIN_EMAIL),
                eq(new ReportMailer.ReportInformation(expectedUrl))
        );
    }

    @Test
    @DisplayName("POST /reports en colecta (FUNDRAISING) al alcanzar el umbral envía el mail con /colectas/{id}")
    @Sql("/sql/common/report-data.sql")
    void reportPost_fundraisingCampaignReachesThreshold_sendsEmailWithFundraisingUrl() throws Exception {
        mockMvc.perform(
                        post("/reports")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(VALID_FUNDRAISING_CAMPAIGN_REPORT_REQUEST))
                )
                .andExpect(status().isCreated());

        String expectedUrl = FRONTEND_URL + "/colectas/" + MOCK_FUNDRAISING_CAMPAIGN_ID;
        verify(reportMailer, times(1)).send(
                eq(ADMIN_EMAIL),
                eq(new ReportMailer.ReportInformation(expectedUrl))
        );
    }

    private String getAccessTokenForUser(String username) throws Exception {
        String loginBody = mockMvc.perform(
                post("/auth/login").header("Authorization", getCredentials(username, "admin"))
        ).andReturn().getResponse().getContentAsString();
        return mapper.readTree(loginBody).get("accessToken").asText();
    }
}