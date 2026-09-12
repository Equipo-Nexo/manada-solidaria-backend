package com.nexo.manada_solidaria_backend.auth.utils;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.users.data.enums.Rol.COMMUNITY;
import static com.nexo.manada_solidaria_backend.users.data.enums.Rol.RESCUER;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

public class MockAuthDataUtils {

    public static final String USERNAME = "admin";
    public static final String NON_EXISTENT_USER = "nonexistentuser";
    public static final String NON_REPEATED_USER = "nonrepeateduser";
    public static final String ADMIN_PASSWORD = "admin";
    public static final String VERY_STRONG_PASSWORD = "VeryStrong1!";
    public static final String WRONG_PASSWORD = "wrongpassword";
    public static final String EMAIL = "test@gmail.com";

    private static final String SEVEN_CHARACTERS = "Aa1!sss";
    private static final String WITHOUT_UPPERCASE = "aa1!ssss";
    private static final String WITHOUT_LOWERCASE = "AA1!SSSS";
    private static final String WITHOUT_DIGIT = "Aa!!ssss";
    private static final String WITHOUT_SPECIAL_CHARACTER = "Aa11ssss";
    private static final String SIXTY_FIVE_CHARACTERS = "Aa1!" + "s".repeat(61);
    private static final String OVER_SEVENTY_TWO_BYTES = "Aa1!" + "ñ".repeat(39);

    private static Stream<Arguments> provideLoginTestCases() {
        return Stream.of(
                Arguments.of("user exists and login successfully", USERNAME, ADMIN_PASSWORD, HttpStatus.OK),
                Arguments.of("user exists but password is wrong", USERNAME, WRONG_PASSWORD, HttpStatus.UNAUTHORIZED),
                Arguments.of("user does not exist", NON_EXISTENT_USER, WRONG_PASSWORD, HttpStatus.UNAUTHORIZED)
        );
    }

    private static Stream<Arguments> provideSignupCases() {
        return Stream.of(
                Arguments.of(
                        "sin roles queda como community",
                        signupWith(NON_EXISTENT_USER, VERY_STRONG_PASSWORD),
                        List.of(COMMUNITY)
                ),
                Arguments.of(
                        "con rol rescuer queda solo como rescuer",
                        CreateUserRequest.builder().username(NON_REPEATED_USER).password(VERY_STRONG_PASSWORD).repeatedPassword(VERY_STRONG_PASSWORD).email(EMAIL).roles(List.of(RESCUER)).build(),
                        List.of(RESCUER)
                )
        );
    }

    private static Stream<Arguments> provideInvalidSignupCases() {
        return Stream.of(
                Arguments.of(
                        "el username ya existe",
                        signupWith(USERNAME, VERY_STRONG_PASSWORD),
                        hasItem(containsString("El nombre de usuario o el correo"))
                ),
                Arguments.of(
                        "las contraseñas no coinciden",
                        CreateUserRequest.builder().username(NON_EXISTENT_USER).password(VERY_STRONG_PASSWORD).repeatedPassword(WRONG_PASSWORD).email(EMAIL).build(),
                        hasItem(containsString("no coinciden"))
                ),
                Arguments.of(
                        "no se envia el email",
                        CreateUserRequest.builder().username(NON_EXISTENT_USER).password(VERY_STRONG_PASSWORD).repeatedPassword(VERY_STRONG_PASSWORD).build(),
                        hasItem(containsString("Debe ingresar un correo"))
                ),
                Arguments.of(
                        "la contraseña tiene siete caracteres",
                        signupWith(NON_EXISTENT_USER, SEVEN_CHARACTERS),
                        hasItem(containsString("8 caracteres"))
                ),
                Arguments.of(
                        "la contraseña no tiene mayuscula",
                        signupWith(NON_EXISTENT_USER, WITHOUT_UPPERCASE),
                        hasItem(containsString("Una letra may"))
                ),
                Arguments.of(
                        "la contraseña no tiene minuscula",
                        signupWith(NON_EXISTENT_USER, WITHOUT_LOWERCASE),
                        hasItem(containsString("Una letra min"))
                ),
                Arguments.of(
                        "la contraseña no tiene numero",
                        signupWith(NON_EXISTENT_USER, WITHOUT_DIGIT),
                        hasItem(containsString("Un n"))
                ),
                Arguments.of(
                        "la contraseña no tiene caracter especial",
                        signupWith(NON_EXISTENT_USER, WITHOUT_SPECIAL_CHARACTER),
                        hasItem(containsString("cter especial"))
                ),
                Arguments.of(
                        "la contraseña tiene sesenta y cinco caracteres",
                        signupWith(NON_EXISTENT_USER, SIXTY_FIVE_CHARACTERS),
                        hasItem(containsString("no puede superar los 64 caracteres"))
                ),
                Arguments.of(
                        "la contraseña supera los setenta y dos bytes",
                        signupWith(NON_EXISTENT_USER, OVER_SEVENTY_TWO_BYTES),
                        hasItem(containsString("no puede superar los 64 caracteres"))
                )
        );
    }

    private static CreateUserRequest signupWith(String username, String password) {
        return CreateUserRequest.builder()
                .username(username)
                .password(password)
                .repeatedPassword(password)
                .email(EMAIL)
                .build();
    }
}
