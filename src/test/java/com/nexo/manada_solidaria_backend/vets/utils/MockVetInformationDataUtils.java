package com.nexo.manada_solidaria_backend.vets.utils;

import com.nexo.manada_solidaria_backend.locations.controllers.requests.UpdateLocationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest.LocationRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.CreateVetInformationRequest.ScheduleRequest;
import com.nexo.manada_solidaria_backend.vets.controllers.requests.UpdateVetInformationRequest;
import org.junit.jupiter.params.provider.Arguments;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
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

    public static final UpdateVetInformationRequest UPDATE_VET_VALID =
            new UpdateVetInformationRequest(
                    "Veterinaria San Roque Actualizada",
                    "3514567899",
                    "nuevo@sanroque.com",
                    "vet-profile-updated",
                    "https://veterinariasanroque.com/nueva",
                    "Nueva descripción de la veterinaria.",
                    new UpdateLocationRequest(
                            "Nueva Sede San Roque",
                            "Av. Nueva",
                            500,
                            -32.4000,
                            -63.2300
                    ),
                    List.of(
                            new UpdateVetInformationRequest.ScheduleRequest(
                                    DayOfWeek.MONDAY,
                                    LocalTime.of(9, 0),
                                    LocalTime.of(13, 0)
                            ),
                            new UpdateVetInformationRequest.ScheduleRequest(
                                    DayOfWeek.FRIDAY,
                                    LocalTime.of(14, 0),
                                    LocalTime.of(19, 0)
                            )
                    )
            );

    private static final UpdateVetInformationRequest UPDATE_VET_WITHOUT_NAME =
            new UpdateVetInformationRequest(
                    null,
                    "3514567899",
                    "nuevo@sanroque.com",
                    null,
                    null,
                    null,
                    new UpdateLocationRequest(
                            "Nueva Sede",
                            null,
                            null,
                            -32.4000,
                            -63.2300
                    ),
                    List.of(
                            new UpdateVetInformationRequest.ScheduleRequest(
                                    DayOfWeek.MONDAY,
                                    LocalTime.of(9, 0),
                                    LocalTime.of(13, 0)
                            )
                    )
            );

    private static final UpdateVetInformationRequest UPDATE_VET_INVALID_PHONE =
            new UpdateVetInformationRequest(
                    "Veterinaria San Roque",
                    "123",
                    "nuevo@sanroque.com",
                    null,
                    null,
                    null,
                    new UpdateLocationRequest(
                            "Nueva Sede",
                            null,
                            null,
                            -32.4000,
                            -63.2300
                    ),
                    List.of(
                            new UpdateVetInformationRequest.ScheduleRequest(
                                    DayOfWeek.MONDAY,
                                    LocalTime.of(9, 0),
                                    LocalTime.of(13, 0)
                            )
                    )
            );

    private static final UpdateVetInformationRequest UPDATE_VET_INVALID_EMAIL =
            new UpdateVetInformationRequest(
                    "Veterinaria San Roque",
                    "3514567899",
                    "email-invalido",
                    null,
                    null,
                    null,
                    new UpdateLocationRequest(
                            "Nueva Sede",
                            null,
                            null,
                            -32.4000,
                            -63.2300
                    ),
                    List.of(
                            new UpdateVetInformationRequest.ScheduleRequest(
                                    DayOfWeek.MONDAY,
                                    LocalTime.of(9, 0),
                                    LocalTime.of(13, 0)
                            )
                    )
            );

    private static final UpdateVetInformationRequest UPDATE_VET_WITHOUT_LOCATION =
            new UpdateVetInformationRequest(
                    "Veterinaria San Roque",
                    "3514567899",
                    "nuevo@sanroque.com",
                    null,
                    null,
                    null,
                    null,
                    List.of(
                            new UpdateVetInformationRequest.ScheduleRequest(
                                    DayOfWeek.MONDAY,
                                    LocalTime.of(9, 0),
                                    LocalTime.of(13, 0)
                            )
                    )
            );

    private static final UpdateVetInformationRequest UPDATE_VET_WITHOUT_CALENDAR =
            new UpdateVetInformationRequest(
                    "Veterinaria San Roque",
                    "3514567899",
                    "nuevo@sanroque.com",
                    null,
                    null,
                    null,
                    new UpdateLocationRequest(
                            "Nueva Sede",
                            null,
                            null,
                            -32.4000,
                            -63.2300
                    ),
                    null
            );

    private static final UpdateVetInformationRequest UPDATE_VET_EMPTY_CALENDAR =
            new UpdateVetInformationRequest(
                    "Veterinaria San Roque",
                    "3514567899",
                    "nuevo@sanroque.com",
                    null,
                    null,
                    null,
                    new UpdateLocationRequest(
                            "Nueva Sede",
                            null,
                            null,
                            -32.4000,
                            -63.2300
                    ),
                    List.of()
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

    public static Stream<Arguments> provideDeleteVetInformationAuthenticationCases() {
        return Stream.of(
                Arguments.of(
                        "Sin token",
                        null
                ),
                Arguments.of(
                        "Token inválido",
                        INVALID_ACCESS_TOKEN
                )
        );
    }

    public static Stream<Arguments> provideDeleteVetInformationInvalidIdCases() {
        return Stream.of(
                Arguments.of(
                        "ID inexistente",
                        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
                ),
                Arguments.of(
                        "Otro ID inexistente",
                        "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
                )
        );
    }

    private static Stream<Arguments> provideUpdateVetInformationInvalidCases() {
        return Stream.of(
                Arguments.of(
                        "Sin nombre devuelve BAD_REQUEST",
                        UPDATE_VET_WITHOUT_NAME
                ),
                Arguments.of(
                        "Teléfono inválido devuelve BAD_REQUEST",
                        UPDATE_VET_INVALID_PHONE
                ),
                Arguments.of(
                        "Email inválido devuelve BAD_REQUEST",
                        UPDATE_VET_INVALID_EMAIL
                ),
                Arguments.of(
                        "Sin ubicación devuelve BAD_REQUEST",
                        UPDATE_VET_WITHOUT_LOCATION
                ),
                Arguments.of(
                        "Sin calendario devuelve BAD_REQUEST",
                        UPDATE_VET_WITHOUT_CALENDAR
                ),
                Arguments.of(
                        "Calendario vacío devuelve BAD_REQUEST",
                        UPDATE_VET_EMPTY_CALENDAR
                )
        );
    }

    private static Stream<Arguments> provideUpdateVetInformationResponseCases() {
        return Stream.of(
                Arguments.of(
                        "Actualiza el nombre",
                        UPDATE_VET_VALID,
                        "$.name",
                        is("Veterinaria San Roque Actualizada")
                ),
                Arguments.of(
                        "Actualiza el teléfono",
                        UPDATE_VET_VALID,
                        "$.phone",
                        is("3514567899")
                ),
                Arguments.of(
                        "Actualiza el email",
                        UPDATE_VET_VALID,
                        "$.email",
                        is("nuevo@sanroque.com")
                ),
                Arguments.of(
                        "Actualiza la imagen de perfil",
                        UPDATE_VET_VALID,
                        "$.profilePictureUrl",
                        is("vet-profile-updated")
                ),
                Arguments.of(
                        "Actualiza la URL de la veterinaria",
                        UPDATE_VET_VALID,
                        "$.vetPageUrl",
                        is("https://veterinariasanroque.com/nueva")
                ),
                Arguments.of(
                        "Actualiza la descripción",
                        UPDATE_VET_VALID,
                        "$.description",
                        is("Nueva descripción de la veterinaria.")
                ),
                Arguments.of(
                        "Actualiza el nombre de la ubicación",
                        UPDATE_VET_VALID,
                        "$.location.name",
                        is("Nueva Sede San Roque")
                ),
                Arguments.of(
                        "Actualiza la dirección de la ubicación",
                        UPDATE_VET_VALID,
                        "$.location.address",
                        is("Av. Nueva")
                ),
                Arguments.of(
                        "Actualiza el número de la ubicación",
                        UPDATE_VET_VALID,
                        "$.location.number",
                        is(500)
                ),
                Arguments.of(
                        "Actualiza la cantidad de días de atención",
                        UPDATE_VET_VALID,
                        "$.calendar.length()",
                        is(2)
                )
        );
    }
}