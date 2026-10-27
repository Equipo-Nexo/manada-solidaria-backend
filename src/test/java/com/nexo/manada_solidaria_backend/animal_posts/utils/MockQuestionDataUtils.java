package com.nexo.manada_solidaria_backend.animal_posts.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;

public class MockQuestionDataUtils {

    public static Stream<Arguments> provideGetQuestionsUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token de autorización", null),
                Arguments.of("Con token inválido o expirado", INVALID_ACCESS_TOKEN)
        );
    }
}