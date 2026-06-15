package com.nexo.manada_solidaria_backend.auth.utils;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.users.data.enums.Rol.RESCUER;

public class MockAuthDataUtils {

    public static final String USERNAME = "admin";
    public static final String NON_EXISTENT_USER = "nonexistentuser";
    public static final String NON_REPEATED_USER = "nonrepeateduser";
    public static final String ADMIN_PASSWORD = "admin";
    public static final String VERY_STRONG_PASSWORD = "verystrongpassword";
    public static final String WRONG_PASSWORD = "wrongpassword";
    public static final String EMAIL = "test@gmail.com";

    private static Stream<Arguments> provideLoginTestCases() {
        return Stream.of(
                Arguments.of("user exists and login successfully", USERNAME, ADMIN_PASSWORD, HttpStatus.OK),
                Arguments.of("user exists but password is wrong", USERNAME, WRONG_PASSWORD, HttpStatus.UNAUTHORIZED),
                Arguments.of("user does not exist", NON_EXISTENT_USER, WRONG_PASSWORD, HttpStatus.UNAUTHORIZED)
        );
    }

    private static Stream<Arguments> provideSignupTestCases() {
        return Stream.of(
                Arguments.of(
                        "username does not exists, user is created successfully",
                        CreateUserRequest.builder().username(NON_EXISTENT_USER).password(VERY_STRONG_PASSWORD).repeatedPassword(VERY_STRONG_PASSWORD).email(EMAIL).build(),
                        HttpStatus.CREATED
                ),
                Arguments.of(
                        "user is rescuer, is saved with role rescuer and not community",
                        CreateUserRequest.builder().username(NON_REPEATED_USER).password(VERY_STRONG_PASSWORD).repeatedPassword(VERY_STRONG_PASSWORD).email(EMAIL).roles(List.of(RESCUER)).build(),
                        HttpStatus.CREATED
                ),
                Arguments.of(
                        "username already exists, return error",
                        CreateUserRequest.builder().username(USERNAME).password(VERY_STRONG_PASSWORD).repeatedPassword(VERY_STRONG_PASSWORD).email(EMAIL).build(),
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        "passwords does not match, return error",
                        CreateUserRequest.builder().username(NON_EXISTENT_USER).password(VERY_STRONG_PASSWORD).repeatedPassword(WRONG_PASSWORD).email(EMAIL).build(),
                        HttpStatus.BAD_REQUEST
                ),
                Arguments.of(
                        "contact information is not sent, return error",
                        CreateUserRequest.builder().username(NON_EXISTENT_USER).password(VERY_STRONG_PASSWORD).repeatedPassword(VERY_STRONG_PASSWORD).build(),
                        HttpStatus.BAD_REQUEST
                )
        );
    }
}
