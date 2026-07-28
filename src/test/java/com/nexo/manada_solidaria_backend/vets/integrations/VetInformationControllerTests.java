package com.nexo.manada_solidaria_backend.vets.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
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
import org.springframework.test.web.servlet.ResultActions;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VetInformationControllerTests extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private VetInformationRepository vetInformationRepository;

    @DisplayName("POST /vets-information refleja en la response los datos enviados")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideCreateVetInformationResponseCases")
    void createVetInformation_returnsCreatedData(
            String testName,
            String body,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        postVetInformation(body)
                .andExpect(status().isCreated())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("POST /vets-information persiste la información y sus relaciones (Location y Calendar)")
    void createVetInformation_persistsEntity() throws Exception {
        postVetInformation(MockVetInformationDataUtils.CREATE_VET_VALID)
                .andExpect(status().isCreated());

        assertThat(vetInformationRepository.findAll()).hasSize(1);

        VetInformation savedVet = vetInformationRepository.findAll().get(0);
        assertThat(savedVet.getName()).isEqualTo("Veterinaria San Roque");
        assertThat(savedVet.getPhone()).isEqualTo("3514567890");
        assertThat(savedVet.getEmail()).isEqualTo("contacto@sanroque.com");

        // Verifica que la Location en cascada se guardó
        assertThat(savedVet.getLocation()).isNotNull();
        assertThat(savedVet.getLocation().getName()).isEqualTo("Sede Central");

        // Verifica que el Calendar en cascada se guardó
        assertThat(savedVet.getCalendar()).hasSize(2);
    }

    @DisplayName("POST /vets-information con payload inválido devuelve 400")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.vets.utils.MockVetInformationDataUtils#provideCreateVetInformationInvalidCases")
    void createVetInformation_invalidPayload_returnsBadRequest(String testName, String body) throws Exception {
        postVetInformation(body).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /vets-information sin token devuelve 401")
    void createVetInformation_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/vets-information")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockVetInformationDataUtils.CREATE_VET_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /vets-information con token inválido devuelve 401")
    void createVetInformation_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/vets-information")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockVetInformationDataUtils.CREATE_VET_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions postVetInformation(String body) throws Exception {
        return mockMvc.perform(
                post("/vets-information")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );
    }
}