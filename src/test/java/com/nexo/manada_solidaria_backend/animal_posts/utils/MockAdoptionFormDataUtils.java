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
}