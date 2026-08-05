package com.nexo.manada_solidaria_backend.vets.utils;

import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest.LocationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest.ScheduleRequest;
import org.junit.jupiter.params.provider.Arguments;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class MockVetInformationDataUtils {

    public static final CreateVetInformationRequest CREATE_VET_VALID = new CreateVetInformationRequest(
            "Veterinaria San Roque",
            "3514567890",
            "contacto@sanroque.com",
            "vet-profile-123",
            "https://veterinariasanroque.com",
            "Atención clínica, vacunación y cirugías.",
            new LocationRequest(
                    "Sede Central",
                    "Av. Libertador",
                    1234,
                    -32.4075,
                    -63.2402
            ),
            List.of(
                    new ScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 30)),
                    new ScheduleRequest(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))
            )
    );

    private static final CreateVetInformationRequest CREATE_VET_WITHOUT_NAME = new CreateVetInformationRequest(
            null,
            "3514567890",
            "contacto@sanroque.com",
            null,
            null,
            null,
            new LocationRequest("Sede Central", null, null, -32.4075, -63.2402),
            null
    );

    private static final CreateVetInformationRequest CREATE_VET_INVALID_PHONE = new CreateVetInformationRequest(
            "Veterinaria San Roque",
            "123",
            "contacto@sanroque.com",
            null,
            null,
            null,
            new LocationRequest("Sede Central", null, null, -32.4075, -63.2402),
            null
    );

    private static final CreateVetInformationRequest CREATE_VET_INVALID_EMAIL = new CreateVetInformationRequest(
            "Veterinaria San Roque",
            "3514567890",
            "email-invalido",
            null,
            null,
            null,
            new LocationRequest("Sede Central", null, null, -32.4075, -63.2402),
            null
    );

    private static final CreateVetInformationRequest CREATE_VET_WITHOUT_LOCATION = new CreateVetInformationRequest(
            "Veterinaria San Roque",
            "3514567890",
            "contacto@sanroque.com",
            null,
            null,
            null,
            null,
            null
    );

    private static final CreateVetInformationRequest CREATE_VET_LOCATION_MISSING_NAME = new CreateVetInformationRequest(
            "Veterinaria San Roque",
            "3514567890",
            "contacto@sanroque.com",
            null,
            null,
            null,
            new LocationRequest(null, null, null, -32.4075, -63.2402),
            null
    );

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

    private static Stream<Arguments> provideGetVetInformationResponseCases() {
        return Stream.of(
                Arguments.of(
                        "Devuelve el nombre",
                        "$.name",
                        is("Veterinaria San Roque")
                ),
                Arguments.of(
                        "Devuelve el teléfono",
                        "$.phone",
                        is("3514567890")
                ),
                Arguments.of(
                        "Devuelve el email",
                        "$.email",
                        is("contacto@sanroque.com")
                ),
                Arguments.of(
                        "Devuelve la imagen de perfil",
                        "$.profilePictureUrl",
                        is("vet-profile-san-roque")
                ),
                Arguments.of(
                        "Devuelve la URL de la veterinaria",
                        "$.vetPageUrl",
                        is("https://veterinariasanroque.com")
                ),
                Arguments.of(
                        "Devuelve la descripción",
                        "$.description",
                        is("Atención clínica, vacunación y cirugías.")
                ),
                Arguments.of(
                        "Devuelve el nombre de la ubicación",
                        "$.location.name",
                        is("Sede San Roque")
                ),
                Arguments.of(
                        "Devuelve la dirección de la ubicación",
                        "$.location.address",
                        is("Av. España")
                ),
                Arguments.of(
                        "Devuelve dos días de atención",
                        "$.calendar.length()",
                        is(2)
                )
        );
    }
}