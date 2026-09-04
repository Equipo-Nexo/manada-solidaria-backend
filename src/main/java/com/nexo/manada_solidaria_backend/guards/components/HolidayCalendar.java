package com.nexo.manada_solidaria_backend.guards.components;

import com.nexo.manada_solidaria_backend.guards.clients.HolidaysRestClient;
import com.nexo.manada_solidaria_backend.guards.clients.responses.HolidayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidayCalendar {

    private final HolidaysRestClient holidaysRestClient;
    private volatile Snapshot snapshot;

    public boolean isHoliday(LocalDate date) {
        Snapshot current = snapshot;
        if (current == null || !current.loadedOn().equals(date)) {
            current = reload(date, current);
        }
        return current.dates().contains(date);
    }

    private Snapshot reload(LocalDate date, Snapshot previous) {
        try {
            Snapshot reloaded = new Snapshot(date, fetch(date.getYear()));
            snapshot = reloaded;
            return reloaded;
        } catch (RestClientException | DateTimeParseException exception) {
            log.error("Error fetching argentine holidays: year={}", date.getYear(), exception);
            return previous != null ? previous : new Snapshot(date, Set.of());
        }
    }

    private Set<LocalDate> fetch(int year) {
        return holidaysRestClient.getHolidays(year)
                .stream()
                .map(HolidayResponse::fecha)
                .filter(Objects::nonNull)
                .map(LocalDate::parse)
                .collect(Collectors.toUnmodifiableSet());
    }

    private record Snapshot(LocalDate loadedOn, Set<LocalDate> dates) {
    }
}
