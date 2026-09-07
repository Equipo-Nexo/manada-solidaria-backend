package com.nexo.manada_solidaria_backend.animal_posts.utils;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;

public class MockAdoptionFormDataUtils {

    public static final String ADOPTION_FORM_VALID_TEMPLATE = """
            {
              "adoptionPostId": "%s",
              "phoneNumber": { "areaCode": "353", "number": "4123456" },
              "questions": [
                { "question": "¿Alquilás? ¿Te permiten mascotas?", "answer": "Alquilo y sí me permiten." },
                { "question": "¿Contás con patio cerrado?", "answer": "Sí, totalmente cerrado." }
              ]
            }
            """;

    private static final String WITHOUT_POST_ID = """
            {
              "phoneNumber": { "areaCode": "353", "number": "4123456" },
              "questions": [
                { "question": "¿Tenés patio?", "answer": "Sí." }
              ]
            }
            """;

    private static final String WITHOUT_PHONE = """
            {
              "adoptionPostId": "%s",
              "questions": [
                { "question": "¿Tenés patio?", "answer": "Sí." }
              ]
            }
            """;

    private static final String WITHOUT_QUESTIONS = """
            {
              "adoptionPostId": "%s",
              "phoneNumber": { "areaCode": "353", "number": "4123456" },
              "questions": []
            }
            """;

    private static final String WITH_BLANK_QUESTION = """
            {
              "adoptionPostId": "%s",
              "phoneNumber": { "areaCode": "353", "number": "4123456" },
              "questions": [
                { "question": "", "answer": "Respuesta sin pregunta" }
              ]
            }
            """;

    private static final String WITH_BLANK_ANSWER = """
            {
              "adoptionPostId": "%s",
              "phoneNumber": { "areaCode": "353", "number": "4123456" },
              "questions": [
                { "question": "Pregunta válida", "answer": "" }
              ]
            }
            """;

    public static String buildValidFormRequest(UUID postId) {
        return String.format(ADOPTION_FORM_VALID_TEMPLATE, postId);
    }

    public static Stream<Arguments> provideCreateFormValidationCases() {
        UUID validPostId = UUID.randomUUID();
        return Stream.of(
                Arguments.of("Sin adoptionPostId devuelve BAD_REQUEST", WITHOUT_POST_ID, HttpStatus.BAD_REQUEST),
                Arguments.of("Sin teléfono devuelve BAD_REQUEST", String.format(WITHOUT_PHONE, validPostId), HttpStatus.BAD_REQUEST),
                Arguments.of("Lista de preguntas vacía devuelve BAD_REQUEST", String.format(WITHOUT_QUESTIONS, validPostId), HttpStatus.BAD_REQUEST),
                Arguments.of("Pregunta vacía devuelve BAD_REQUEST", String.format(WITH_BLANK_QUESTION, validPostId), HttpStatus.BAD_REQUEST),
                Arguments.of("Respuesta vacía devuelve BAD_REQUEST", String.format(WITH_BLANK_ANSWER, validPostId), HttpStatus.BAD_REQUEST)
        );
    }

    public static Stream<Arguments> provideCreateFormUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token de autenticación devuelve 401", null),
                Arguments.of("Con token inválido devuelve 401", INVALID_ACCESS_TOKEN)
        );
    }
}