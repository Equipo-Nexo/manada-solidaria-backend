package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class QuestionControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.animal_posts.utils.MockQuestionDataUtils#";

    @Test
    @DisplayName("GET /questions trae las categorías agrupadas con sus preguntas y detalles ordenadas")
    @Sql("/sql/animal_posts/get-questions.sql")
    void getQuestions_returnsOrderedCategoriesAndQuestions() throws Exception {
        mockMvc.perform(
                        get("/questions")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category").value("Tu hogar"))
                .andExpect(jsonPath("$[0].description").value("Queremos conocer el lugar donde vivirá el animal."))
                .andExpect(jsonPath("$[0].questions[0].title").value("Tipo de vivienda"))
                .andExpect(jsonPath("$[0].questions[0].type").value("SELECTION"))
                .andExpect(jsonPath("$[0].questions[0].iconName").value("Home"))
                .andExpect(jsonPath("$[0].questions[0].placeHolder").value("Seleccioná una opción"))
                .andExpect(jsonPath("$[0].questions[0].details[0].description").value("Casa"))
                .andExpect(jsonPath("$[0].questions[0].details[1].description").value("Departamento"))
                .andExpect(jsonPath("$[1].category").value("Sobre la adopción"))
                .andExpect(jsonPath("$[1].questions[0].title").value("¿Tenés experiencia previa con mascotas?"));
    }

    @DisplayName("GET /questions sin token o token inválido responde 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGetQuestionsUnauthorizedCases")
    void getQuestions_unauthorized(String testName, String token) throws Exception {
        var request = get("/questions");
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}