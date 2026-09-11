package com.nexo.manada_solidaria_backend.animal_posts.utils;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;

public class MockAdoptionFormDataUtils {

    public static final UUID POST_WITH_FORMS_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    public static final UUID POST_WITHOUT_FORMS_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    public static final UUID NON_EXISTENT_POST_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static CreateAdoptionFormRequest createValidRequest(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                new PhoneNumberRequest("353", "4123456"),
                List.of(
                        new CreateAdoptionFormRequest.QuestionFormRequest("¿Alquilás? ¿Te permiten mascotas?", "Alquilo y sí me permiten."),
                        new CreateAdoptionFormRequest.QuestionFormRequest("¿Contás con patio cerrado?", "Sí, totalmente cerrado.")
                )
        );
    }

    public static CreateAdoptionFormRequest createWithoutPostId() {
        return new CreateAdoptionFormRequest(
                null,
                new PhoneNumberRequest("353", "4123456"),
                List.of(new CreateAdoptionFormRequest.QuestionFormRequest("¿Tenés patio?", "Sí."))
        );
    }

    public static CreateAdoptionFormRequest createWithoutPhone(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                null,
                List.of(new CreateAdoptionFormRequest.QuestionFormRequest("¿Tenés patio?", "Sí."))
        );
    }

    public static CreateAdoptionFormRequest createWithoutQuestions(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                new PhoneNumberRequest("353", "4123456"),
                Collections.emptyList()
        );
    }

    public static CreateAdoptionFormRequest createWithBlankQuestion(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                new PhoneNumberRequest("353", "4123456"),
                List.of(new CreateAdoptionFormRequest.QuestionFormRequest("", "Respuesta sin pregunta"))
        );
    }

    public static CreateAdoptionFormRequest createWithBlankAnswer(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                new PhoneNumberRequest("353", "4123456"),
                List.of(new CreateAdoptionFormRequest.QuestionFormRequest("Pregunta válida", ""))
        );
    }

    public static Stream<Arguments> provideCreateFormValidationCases() {
        UUID postId = UUID.randomUUID();
        return Stream.of(
                Arguments.of("Sin id de publicación", createWithoutPostId(), HttpStatus.BAD_REQUEST),
                Arguments.of("Sin teléfono de contacto", createWithoutPhone(postId), HttpStatus.BAD_REQUEST),
                Arguments.of("Sin preguntas respondidas", createWithoutQuestions(postId), HttpStatus.BAD_REQUEST),
                Arguments.of("Pregunta en blanco", createWithBlankQuestion(postId), HttpStatus.BAD_REQUEST),
                Arguments.of("Respuesta en blanco", createWithBlankAnswer(postId), HttpStatus.BAD_REQUEST)
        );
    }

    public static Stream<Arguments> provideCreateFormUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token de autorización", null),
                Arguments.of("Con token inválido o expirado", INVALID_ACCESS_TOKEN)
        );
    }

    public static Stream<Arguments> provideGetFormsByPostIdCases() {
        return Stream.of(
                Arguments.of("Publicación con 1 formulario", POST_WITH_FORMS_ID, 1),
                Arguments.of("Publicación sin formularios asociados", POST_WITHOUT_FORMS_ID, 0)
        );
    }

    public static Stream<Arguments> provideGetFormsByPostIdErrorCases() {
        return Stream.of(
                Arguments.of("Publicación inexistente devuelve NOT_FOUND", NON_EXISTENT_POST_ID, HttpStatus.NOT_FOUND)
        );
    }

    public static Stream<Arguments> provideGetFormsUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token de autorización", null),
                Arguments.of("Con token inválido o expirado", INVALID_ACCESS_TOKEN)
        );
    }
}
