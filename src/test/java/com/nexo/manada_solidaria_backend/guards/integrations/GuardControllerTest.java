package com.nexo.manada_solidaria_backend.guards.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.guards.clients.responses.HolidayResponse;
import com.nexo.manada_solidaria_backend.guards.utils.MockGuardDataUtils.HolidaysApi;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.nexo.manada_solidaria_backend.guards.utils.MockGuardDataUtils.MALFORMED_HOLIDAYS_BODY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GuardControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.guards.utils.MockGuardDataUtils#";
    private static final ZoneId ARGENTINA = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final String HOLIDAYS_PATH = "/v1/feriados/%s";

    @MockitoBean
    private Clock clock;

    @DisplayName("GET /guards/status resuelve el horario de la guardia")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGuardStatusCases")
    void guardStatus(String testName, LocalDateTime moment, HolidaysApi holidaysApi, boolean expected) throws Exception {
        freezeClockAt(moment);
        enqueueHolidays(moment.toLocalDate(), holidaysApi);

        performGuardStatus(accessToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(expected));
    }

    @DisplayName("GET /guards/status sin credenciales validas")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUnauthorizedCases")
    void guardStatusUnauthorized(String testName, String token) throws Exception {
        performGuardStatus(token).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Los feriados se piden una sola vez por dia")
    void holidaysAreFetchedOncePerDay() throws Exception {
        LocalDateTime moment = LocalDateTime.parse("2026-09-08T10:00");
        freezeClockAt(moment);
        enqueueHolidays(moment.toLocalDate(), HolidaysApi.WORKDAY);
        int requestsBefore = MOCK_WEB_SERVER.getRequestCount();

        performGuardStatus(accessToken).andExpect(status().isOk());
        performGuardStatus(accessToken).andExpect(status().isOk());

        assertThat(MOCK_WEB_SERVER.getRequestCount() - requestsBefore).isEqualTo(1);
    }

    private ResultActions performGuardStatus(String token) throws Exception {
        MockHttpServletRequestBuilder request = get("/guards/status");
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(request);
    }

    private void freezeClockAt(LocalDateTime moment) {
        given(clock.instant()).willReturn(moment.atZone(ARGENTINA).toInstant());
        given(clock.getZone()).willReturn(ARGENTINA);
    }

    private void enqueueHolidays(LocalDate date, HolidaysApi holidaysApi) {
        String path = HOLIDAYS_PATH.formatted(date.getYear());
        switch (holidaysApi) {
            case HOLIDAY -> enqueueJsonResponse(path, List.of(new HolidayResponse(date.toString())));
            case WORKDAY -> enqueueJsonResponse(path, List.of());
            case MALFORMED -> enqueueResponse(path, new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .setBody(MALFORMED_HOLIDAYS_BODY));
            case DOWN -> { }
        }
    }
}
