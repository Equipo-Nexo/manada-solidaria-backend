package com.nexo.manada_solidaria_backend.users.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class MockUserDataUtils {

    public static final String UPDATE_PROFILE_VALID = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "nuevo@mail.com",
              "phoneNumber": "1133334444",
              "profileImageURL": "cf-profile-1"
            }
            """;

    private static final String UPDATE_PROFILE_WITHOUT_IMAGE = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "nuevo@mail.com",
              "phoneNumber": "1133334444"
            }
            """;

    private static final String UPDATE_PROFILE_WITHOUT_EMAIL = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "phoneNumber": "1133334444"
            }
            """;

    private static final String UPDATE_PROFILE_INVALID_EMAIL = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "no-es-un-email",
              "phoneNumber": "1133334444"
            }
            """;

    private static final String UPDATE_PROFILE_INVALID_PHONE = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "nuevo@mail.com",
              "phoneNumber": "telefono-invalido"
            }
            """;

    private static Stream<Arguments> provideGetUserPostsTestCases() {
        return Stream.of(
                Arguments.of("Get all user posts", null, 3),
                Arguments.of("Get all user posts", "animal", 1),
                Arguments.of("Get all user posts", "campaign", 2)
        );
    }

    private static Stream<Arguments> provideUpdateProfileInvalidCases() {
        return Stream.of(
                Arguments.of("Sin email devuelve BAD_REQUEST", UPDATE_PROFILE_WITHOUT_EMAIL),
                Arguments.of("Email con formato invalido devuelve BAD_REQUEST", UPDATE_PROFILE_INVALID_EMAIL),
                Arguments.of("Telefono con formato invalido devuelve BAD_REQUEST", UPDATE_PROFILE_INVALID_PHONE)
        );
    }

    private static Stream<Arguments> provideUpdateProfileResponseCases() {
        return Stream.of(
                Arguments.of("Devuelve el name enviado", UPDATE_PROFILE_VALID, "$.name", is("Elian")),
                Arguments.of("Devuelve el lastname enviado", UPDATE_PROFILE_VALID, "$.lastname", is("Enria")),
                Arguments.of("Devuelve el email enviado", UPDATE_PROFILE_VALID, "$.email", is("nuevo@mail.com")),
                Arguments.of("Devuelve el phoneNumber enviado", UPDATE_PROFILE_VALID, "$.phoneNumber", is("1133334444")),
                Arguments.of("Devuelve el profileImageURL enviado", UPDATE_PROFILE_VALID, "$.profileImageURL", is("cf-profile-1")),
                Arguments.of("Reemplazo total: el campo omitido queda null", UPDATE_PROFILE_WITHOUT_IMAGE, "$.profileImageURL", nullValue())
        );
    }
}
