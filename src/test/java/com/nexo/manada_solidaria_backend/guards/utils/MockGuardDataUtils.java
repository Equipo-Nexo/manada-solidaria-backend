package com.nexo.manada_solidaria_backend.guards.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static com.nexo.manada_solidaria_backend.guards.utils.MockGuardDataUtils.HolidaysApi.DOWN;
import static com.nexo.manada_solidaria_backend.guards.utils.MockGuardDataUtils.HolidaysApi.HOLIDAY;
import static com.nexo.manada_solidaria_backend.guards.utils.MockGuardDataUtils.HolidaysApi.MALFORMED;
import static com.nexo.manada_solidaria_backend.guards.utils.MockGuardDataUtils.HolidaysApi.WORKDAY;

public class MockGuardDataUtils {

    public static final String MALFORMED_HOLIDAYS_BODY = """
            [{"fecha":null},{"fecha":"no-es-una-fecha"}]""";

    public enum HolidaysApi {
        HOLIDAY,
        WORKDAY,
        DOWN,
        MALFORMED
    }

    private static Stream<Arguments> provideGuardStatusCases() {
        return Stream.of(
                Arguments.of("Lunes 08:29, todavia es la guardia del domingo", moment("2026-09-07T08:29"), WORKDAY, true),
                Arguments.of("Lunes 08:31, ya abrieron las veterinarias", moment("2026-09-14T08:31"), WORKDAY, false),
                Arguments.of("Lunes 12:59, todavia no arranco la siesta", moment("2026-10-05T12:59"), WORKDAY, false),
                Arguments.of("Lunes 13:00, el borde de la siesta es inclusivo", moment("2026-09-21T13:00"), WORKDAY, true),
                Arguments.of("Lunes 15:59, ultimo minuto de la siesta", moment("2026-10-12T15:59"), WORKDAY, true),
                Arguments.of("Lunes 16:00, el borde de la siesta es exclusivo", moment("2026-09-28T16:00"), WORKDAY, false),
                Arguments.of("Miercoles 07:59, sigue la nocturna", moment("2026-09-09T07:59"), WORKDAY, true),
                Arguments.of("Miercoles 08:00, termina la nocturna", moment("2026-09-16T08:00"), WORKDAY, false),
                Arguments.of("Miercoles 19:59, todavia no arranco la nocturna", moment("2026-10-14T19:59"), WORKDAY, false),
                Arguments.of("Miercoles 20:00, el borde de la nocturna es inclusivo", moment("2026-10-21T20:00"), WORKDAY, true),
                Arguments.of("Miercoles 21:00, empezo la nocturna", moment("2026-09-23T21:00"), WORKDAY, true),
                Arguments.of("Viernes 23:00, nocturna de dia de semana", moment("2026-09-11T23:00"), WORKDAY, true),
                Arguments.of("Sabado 03:00, sigue la nocturna del viernes", moment("2026-09-05T03:00"), WORKDAY, true),
                Arguments.of("Sabado 10:00, el hueco de la manana", moment("2026-09-12T10:00"), WORKDAY, false),
                Arguments.of("Sabado 12:59, ultimo minuto del hueco", moment("2026-10-03T12:59"), WORKDAY, false),
                Arguments.of("Sabado 13:00, el borde del fin de semana es inclusivo", moment("2026-10-10T13:00"), WORKDAY, true),
                Arguments.of("Sabado 14:00, arranca la guardia del fin de semana", moment("2026-09-19T14:00"), WORKDAY, true),
                Arguments.of("Sabado 18:00, el sabado no tiene el corte de la siesta", moment("2026-09-26T18:00"), WORKDAY, true),
                Arguments.of("Domingo 11:00, guardia todo el dia", moment("2026-09-06T11:00"), WORKDAY, true),
                Arguments.of("Miercoles 10:00 sin feriado", moment("2026-09-02T10:00"), WORKDAY, false),
                Arguments.of("Miercoles 10:00 feriado, pisa el horario", moment("2026-09-30T10:00"), HOLIDAY, true),
                Arguments.of("Miercoles 10:00 con la api de feriados caida", moment("2026-10-07T10:00"), DOWN, false),
                Arguments.of("Domingo 11:00 con la api de feriados caida", moment("2026-10-11T11:00"), DOWN, true),
                Arguments.of("Miercoles 10:00 con la api devolviendo fechas invalidas", moment("2026-10-28T10:00"), MALFORMED, false)
        );
    }

    private static Stream<Arguments> provideUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token", null),
                Arguments.of("Con token invalido", INVALID_ACCESS_TOKEN)
        );
    }

    private static LocalDateTime moment(String isoDateTime) {
        return LocalDateTime.parse(isoDateTime);
    }
}
