package com.nexo.manada_solidaria_backend.users.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class MockUserDataUtils {
    private static Stream<Arguments> provideGetUserPostsTestCases() {
        return Stream.of(
                Arguments.of("Get all user posts", null, 3),
                Arguments.of("Get all user posts", "animal", 1),
                Arguments.of("Get all user posts", "campaign", 2)
        );
    }
}
