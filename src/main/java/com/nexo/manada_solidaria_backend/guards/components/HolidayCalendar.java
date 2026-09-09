package com.nexo.manada_solidaria_backend.guards.components;

import com.nexo.manada_solidaria_backend.guards.clients.ArgentinaDatosRestClient;
import com.nexo.manada_solidaria_backend.guards.clients.responses.HolidayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidayCalendar {

    private final ArgentinaDatosRestClient argentinaDatosRestClient;
    private volatile CachedAnswer cachedAnswer;

    public boolean isHoliday(LocalDate date) {
        CachedAnswer current = cachedAnswer;
        if (current == null || !current.date().equals(date)) {
            current = reload(date);
        }
        return current.isHoliday();
    }

    private CachedAnswer reload(LocalDate date) {
        try {
            CachedAnswer answer = new CachedAnswer(date, fetchIsHoliday(date));
            cachedAnswer = answer;
            return answer;
        } catch (RestClientException | DateTimeParseException exception) {
            log.error("Error fetching argentine holidays: year={}", date.getYear(), exception);
            return new CachedAnswer(date, false);
        }
    }

    private boolean fetchIsHoliday(LocalDate date) {
        return argentinaDatosRestClient.getHolidays(date.getYear())
                .stream()
                .map(HolidayResponse::fecha)
                .filter(Objects::nonNull)
                .map(LocalDate::parse)
                .anyMatch(date::equals);
    }

    private record CachedAnswer(LocalDate date, boolean isHoliday) {
    }
}
