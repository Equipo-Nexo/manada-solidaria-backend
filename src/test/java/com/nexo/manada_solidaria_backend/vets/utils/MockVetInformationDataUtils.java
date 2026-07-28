package com.nexo.manada_solidaria_backend.vets.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class MockVetInformationDataUtils {

    public static final String CREATE_VET_VALID = """
            {
              "name": "Veterinaria San Roque",
              "phone": "3514567890",
              "email": "contacto@sanroque.com",
              "profilePictureUrl": "vet-profile-123",
              "vetPageUrl": "https://veterinariasanroque.com",
              "description": "Atención clínica, vacunación y cirugías.",
              "location": {
                "name": "Sede Central",
                "address": "Av. Libertador",
                "number": 1234,
                "latitude": -32.4075,
                "longitude": -63.2402
              },
              "calendar": [
                {
                  "dayOfWeek": "MONDAY",
                  "openingTime": "08:00:00",
                  "closingTime": "12:30:00"
                },
                {
                  "dayOfWeek": "TUESDAY",
                  "openingTime": "08:00:00",
                  "closingTime": "18:00:00"
                }
              ]
            }
            """;

    private static final String CREATE_VET_WITHOUT_NAME = """
            {
              "phone": "3514567890",
              "email": "contacto@sanroque.com",
              "location": {
                "name": "Sede Central",
                "latitude": -32.4075,
                "longitude": -63.2402
              }
            }
            """;

    private static final String CREATE_VET_INVALID_PHONE = """
            {
              "name": "Veterinaria San Roque",
              "phone": "123",
              "email": "contacto@sanroque.com",
              "location": {
                "name": "Sede Central",
                "latitude": -32.4075,
                "longitude": -63.2402
              }
            }
            """;

    private static final String CREATE_VET_INVALID_EMAIL = """
            {
              "name": "Veterinaria San Roque",
              "phone": "3514567890",
              "email": "email-invalido",
              "location": {
                "name": "Sede Central",
                "latitude": -32.4075,
                "longitude": -63.2402
              }
            }
            """;

    private static final String CREATE_VET_WITHOUT_LOCATION = """
            {
              "name": "Veterinaria San Roque",
              "phone": "3514567890",
              "email": "contacto@sanroque.com"
            }
            """;

    private static final String CREATE_VET_LOCATION_MISSING_NAME = """
            {
              "name": "Veterinaria San Roque",
              "phone": "3514567890",
              "email": "contacto@sanroque.com",
              "location": {
                "latitude": -32.4075,
                "longitude": -63.2402
              }
            }
            """;

    private static Stream<Arguments> provideCreateVetInformationResponseCases() {
        return Stream.of(
                Arguments.of("Devuelve id generado", CREATE_VET_VALID, "$.id", notNullValue()),
                Arguments.of("Devuelve el name enviado", CREATE_VET_VALID, "$.name", is("Veterinaria San Roque")),
                Arguments.of("Devuelve el phone enviado", CREATE_VET_VALID, "$.phone", is("3514567890")),
                Arguments.of("Devuelve el email enviado", CREATE_VET_VALID, "$.email", is("contacto@sanroque.com")),
                Arguments.of("Devuelve el nombre de la ubicación", CREATE_VET_VALID, "$.location.name", is("Sede Central")),
                Arguments.of("Devuelve la cantidad correcta de días de atención", CREATE_VET_VALID, "$.calendar.length()", is(2))
        );
    }

    private static Stream<Arguments> provideCreateVetInformationInvalidCases() {
        return Stream.of(
                Arguments.of("Sin nombre devuelve BAD_REQUEST", CREATE_VET_WITHOUT_NAME),
                Arguments.of("Teléfono fuera del rango (8-15) devuelve BAD_REQUEST", CREATE_VET_INVALID_PHONE),
                Arguments.of("Email inválido devuelve BAD_REQUEST", CREATE_VET_INVALID_EMAIL),
                Arguments.of("Sin ubicación devuelve BAD_REQUEST", CREATE_VET_WITHOUT_LOCATION),
                Arguments.of("Ubicación sin nombre devuelve BAD_REQUEST", CREATE_VET_LOCATION_MISSING_NAME)
        );
    }
}