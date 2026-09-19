package com.nexo.manada_solidaria_backend.vets.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.data.models.Schedule;
import com.nexo.manada_solidaria_backend.vets.data.models.VetInformation;
import com.nexo.manada_solidaria_backend.vets.data.repositories.ScheduleRepository;
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

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetInformationControllerTests extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private VetInformationRepository vetInformationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

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
        assertThat(savedVet.getPhoneNumber().areaCode()).isEqualTo("3514");
        assertThat(savedVet.getPhoneNumber().number()).isEqualTo("567890");
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

    @DisplayName("GET /vets con filtros y ordenamiento opcionales")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideGetAllVetInformationCases")
    @Sql(
            scripts = "/sql/vets/create-vets.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getVetInformation_withFiltersAndSorting_returnsFilteredList(
            String testName,
            org.springframework.util.MultiValueMap<String, String> params,
            int expectedSize,
            List<String> expectedNamesInOrder
    ) throws Exception {

        var requestBuilder = get("/vets")
                .header("Authorization", "Bearer " + accessToken);

        params.forEach((key, values) -> {
            for (String value : values) {
                requestBuilder.param(key, value);
            }
        });

        var resultActions = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedSize)));

        for (int i = 0; i < expectedNamesInOrder.size(); i++) {
            resultActions.andExpect(jsonPath("$[" + i + "].name").value(expectedNamesInOrder.get(i)));
        }
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

    @DisplayName("DELETE /vets/{vetId} sin autenticación válida devuelve 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideDeleteVetInformationAuthenticationCases")
    void deleteVetInformation_withoutValidAuthentication_returnsUnauthorized(
            String testName,
            String token
    ) throws Exception {

        var request = delete("/vets/44444444-4444-4444-4444-444444444444");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /vets/{vetId} elimina la veterinaria")
    @Sql(
            scripts = "/sql/vets/create-vets.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void deleteVetInformation_deletesVet() throws Exception {

        UUID vetId = UUID.fromString(
                "44444444-4444-4444-4444-444444444444"
        );

        mockMvc.perform(
                        delete("/vets/" + vetId)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNoContent());

        assertThat(vetInformationRepository.findById(vetId))
                .isEmpty();
    }

    @Test
    @DisplayName("DELETE /vets/{vetId} con ID inexistente devuelve 404")
    void deleteVetInformation_withNonExistingId_returnsNotFound() throws Exception {

        mockMvc.perform(
                        delete("/vets/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("DELETE /vets/{vetId} con ID inexistente devuelve 404")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideDeleteVetInformationInvalidIdCases")
    void deleteVetInformation_withNonExistingId_returnsNotFound(
            String testName,
            String vetId
    ) throws Exception {

        mockMvc.perform(
                        delete("/vets/" + vetId)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("PUT /vets/{vetId} refleja en la response los datos actualizados")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideUpdateVetInformationResponseCases")
    @Sql(
            scripts = "/sql/vets/create-vets.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateVetInformation_returnsUpdatedData(
            String testName,
            UpdateVetInformationRequest request,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        updateVetInformation(
                "44444444-4444-4444-4444-444444444444",
                request
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("PUT /vets/{vetId} persiste la información actualizada y sus relaciones")
    @Sql(
            scripts = "/sql/vets/create-vets.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateVetInformation_persistsUpdatedEntity() throws Exception {
        updateVetInformation(
                "44444444-4444-4444-4444-444444444444",
                MockVetInformationDataUtils.UPDATE_VET_VALID
        )
                .andExpect(status().isOk());

        VetInformation updatedVet = vetInformationRepository
                .findById(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .orElseThrow();

        assertThat(updatedVet.getName()).isEqualTo("Veterinaria San Roque Actualizada");
        assertThat(updatedVet.getPhoneNumber().areaCode()).isEqualTo("3514");
        assertThat(updatedVet.getPhoneNumber().number()).isEqualTo("567899");
        assertThat(updatedVet.getEmail()).isEqualTo("nuevo@sanroque.com");
        assertThat(updatedVet.getDescription()).isEqualTo("Nueva descripción de la veterinaria.");

        assertThat(updatedVet.getLocation()).isNotNull();
        assertThat(updatedVet.getLocation().getName()).isEqualTo("Nueva Sede San Roque");
        assertThat(updatedVet.getLocation().getAddress()).isEqualTo("Av. Nueva");
        assertThat(updatedVet.getLocation().getNumber()).isEqualTo(500);

        assertThat(updatedVet.getCalendar()).hasSize(2);

        assertThat(updatedVet.getCalendar())
                .extracting(Schedule::getDayOfWeek)
                .containsExactlyInAnyOrder(
                        DayOfWeek.MONDAY,
                        DayOfWeek.FRIDAY
                );
    }

    @DisplayName("PUT /vets/{vetId} con payload inválido devuelve 400")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideUpdateVetInformationInvalidCases")
    @Sql(
            scripts = "/sql/vets/create-vets.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateVetInformation_invalidPayload_returnsBadRequest(
            String testName,
            UpdateVetInformationRequest request
    ) throws Exception {
        updateVetInformation(
                "44444444-4444-4444-4444-444444444444",
                request
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /vets/{vetId} con ID inexistente devuelve 404")
    void updateVetInformation_withNonExistingId_returnsNotFound() throws Exception {
        updateVetInformation(
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                MockVetInformationDataUtils.UPDATE_VET_VALID
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /vets/{vetId} sin token devuelve 401")
    void updateVetInformation_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/vets/44444444-4444-4444-4444-444444444444")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockVetInformationDataUtils.UPDATE_VET_VALID))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /vets/{vetId} con token inválido devuelve 401")
    void updateVetInformation_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/vets/44444444-4444-4444-4444-444444444444")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockVetInformationDataUtils.UPDATE_VET_VALID))
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

    private ResultActions updateVetInformation(
            String vetId,
            UpdateVetInformationRequest request
    ) throws Exception {
        return mockMvc.perform(
                put("/vets/{vetId}", vetId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
        );
    }
}