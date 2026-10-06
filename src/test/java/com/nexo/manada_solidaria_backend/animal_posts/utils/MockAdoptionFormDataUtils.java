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

    public static final UUID FORM_ID = UUID.fromString("c1111111-1111-1111-1111-111111111111");
    public static final UUID POST_WITH_FORMS_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    public static final UUID POST_WITHOUT_FORMS_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    public static final UUID NON_EXISTENT_POST_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static CreateAdoptionFormRequest createValidRequest(UUID postId, UUID questionId1, UUID questionId2) {
        return new CreateAdoptionFormRequest(
                postId,
                "Quiero adoptar una gatita para que crezca con mi gato.",
                new PhoneNumberRequest("353", "4123456"),
                List.of(
                        new CreateAdoptionFormRequest.AnswerFormRequest(questionId1, "Alquilo y sí me permiten."),
                        new CreateAdoptionFormRequest.AnswerFormRequest(questionId2, "Sí, totalmente cerrado.")
                )
        );
    }

    public static CreateAdoptionFormRequest createValidRequest(UUID postId) {
        return createValidRequest(postId, UUID.randomUUID(), UUID.randomUUID());
    }

    public static CreateAdoptionFormRequest createWithoutPostId() {
        return new CreateAdoptionFormRequest(
                null,
                "Quiero adoptar una mascota.",
                new PhoneNumberRequest("353", "4123456"),
                List.of(new CreateAdoptionFormRequest.AnswerFormRequest(UUID.randomUUID(), "Sí."))
        );
    }

    public static CreateAdoptionFormRequest createWithoutPhone(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                "Quiero adoptar una mascota.",
                null,
                List.of(new CreateAdoptionFormRequest.AnswerFormRequest(UUID.randomUUID(), "Sí."))
        );
    }

    public static CreateAdoptionFormRequest createWithoutQuestions(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                "Quiero adoptar una mascota.",
                new PhoneNumberRequest("353", "4123456"),
                Collections.emptyList()
        );
    }

    public static CreateAdoptionFormRequest createWithoutQuestionFormId(UUID postId) {
        return new CreateAdoptionFormRequest(
                postId,
                "Quiero adoptar una mascota.",
                new PhoneNumberRequest("353", "4123456"),
                List.of(new CreateAdoptionFormRequest.AnswerFormRequest(null, "Respuesta sin id de pregunta"))
        );
    }

    public static Stream<Arguments> provideCreateFormValidationCases() {
        UUID postId = UUID.randomUUID();
        return Stream.of(
                Arguments.of("Sin id de publicación", createWithoutPostId(), HttpStatus.BAD_REQUEST),
                Arguments.of("Sin teléfono de contacto", createWithoutPhone(postId), HttpStatus.BAD_REQUEST),
                Arguments.of("Sin preguntas respondidas", createWithoutQuestions(postId), HttpStatus.BAD_REQUEST),
                Arguments.of("Sin id de pregunta asociada", createWithoutQuestionFormId(postId), HttpStatus.BAD_REQUEST)
        );
    }

    public static Stream<Arguments> provideCreateFormUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token de autorización", null),
                Arguments.of("Con token inválido o expirado", INVALID_ACCESS_TOKEN)
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

    public static Stream<Arguments> provideGetFormsByUserFilterCases() {
        return Stream.of(
                Arguments.of("Filtro OWNER retorna formularios completados por el usuario", "OWNER", 1),
                Arguments.of("Filtro REVIEWER retorna formularios a revisar de sus publicaciones", "REVIEWER", 0)
        );
    }

    public static Stream<Arguments> provideGetFormsByUserUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token de autorización", null),
                Arguments.of("Con token inválido o expirado", INVALID_ACCESS_TOKEN)
        );
    }
}