package com.nexo.manada_solidaria_backend.vets.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest; // <-- 1. Importación necesaria
import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;
import com.nexo.manada_solidaria_backend.vets.data.repositories.VetInformationRepository;
import com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetInformationControllerTests extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private VetInformationRepository vetInformationRepository;

    @DisplayName("POST /vets refleja en la response los datos enviados")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideCreateVetInformationResponseCases")
    void createVetInformation_returnsCreatedData(
            String testName,
            CreateVetInformationRequest request,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        postVetInformation(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("POST /vets persiste la información y sus relaciones (Location y Calendar)")
    void createVetInformation_persistsEntity() throws Exception {
        postVetInformation(MockVetInformationDataUtils.CREATE_VET_VALID)
                .andExpect(status().isCreated());

        assertThat(vetInformationRepository.findAll()).hasSize(1);

        VetInformation savedVet = vetInformationRepository.findAll().get(0);
        assertThat(savedVet.getName()).isEqualTo("Veterinaria San Roque");
        assertThat(savedVet.getPhone()).isEqualTo("3514567890");
        assertThat(savedVet.getEmail()).isEqualTo("contacto@sanroque.com");

        assertThat(savedVet.getLocation()).isNotNull();
        assertThat(savedVet.getLocation().getName()).isEqualTo("Sede Central");

        assertThat(savedVet.getCalendar()).hasSize(2);
    }

    @DisplayName("POST /vets con payload inválido devuelve 400")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideCreateVetInformationInvalidCases")
    void createVetInformation_invalidPayload_returnsBadRequest(
            String testName,
            CreateVetInformationRequest request
    ) throws Exception {
        postVetInformation(request).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /vets sin token devuelve 401")
    void createVetInformation_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/vets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockVetInformationDataUtils.CREATE_VET_VALID))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /vets con token inválido devuelve 401")
    void createVetInformation_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/vets")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockVetInformationDataUtils.CREATE_VET_VALID))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /vets devuelve todas las veterinarias ordenadas alfabéticamente")
    @Sql(
            scripts = "/sql/vets/create-vets.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getVetInformation_returnsAllVetsOrderedByName() throws Exception {
        mockMvc.perform(
                        get("/vets")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Veterinaria Animalia"))
                .andExpect(jsonPath("$[1].name").value("Veterinaria El Sol"))
                .andExpect(jsonPath("$[2].name").value("Veterinaria San Roque"));
    }

    @Test
    @DisplayName("GET /vets sin veterinarias devuelve una lista vacía")
    void getVetInformation_withoutVets_returnsEmptyList() throws Exception {
        mockMvc.perform(
                        get("/vets")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /vets sin token devuelve 401")
    void getVetInformation_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/vets")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /vets con token inválido devuelve 401")
    void getVetInformation_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/vets")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("GET /vets/{vetId} devuelve los datos de la veterinaria")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideGetVetInformationResponseCases")
    @Sql(
            scripts = "/sql/vets/create-vets.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getVetInformationById_returnsVetInformation(
            String testName,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        mockMvc.perform(
                        get("/vets/44444444-4444-4444-4444-444444444444")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("GET /vets/{vetId} con ID inexistente devuelve 404")
    void getVetInformationById_withNonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(
                        get("/vets/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /vets/{vetId} sin token devuelve 401")
    void getVetInformationById_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/vets/44444444-4444-4444-4444-444444444444")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /vets/{vetId} con token inválido devuelve 401")
    void getVetInformationById_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/vets/44444444-4444-4444-4444-444444444444")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions postVetInformation(CreateVetInformationRequest request) throws Exception {
        return mockMvc.perform(
                post("/vets")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
        );
    }
}