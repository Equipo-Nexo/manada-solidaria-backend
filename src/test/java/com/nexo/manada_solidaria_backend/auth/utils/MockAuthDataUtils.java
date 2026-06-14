package com.nexo.manada_solidaria_backend.auth.utils;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public class MockAuthDataUtils {
    private static Stream<Arguments> provideLoginTestCases() {
        return Stream.of(
                Arguments.of("user exists and login successfully", "admin", "admin", HttpStatus.OK),
                Arguments.of("user exists but password is wrong", "admin", "wrongpassword", HttpStatus.UNAUTHORIZED),
                Arguments.of("user does not exist", "nonexistentuser", "anyPassword", HttpStatus.UNAUTHORIZED)
        );
    }
}
