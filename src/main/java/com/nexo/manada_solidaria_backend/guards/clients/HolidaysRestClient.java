package com.nexo.manada_solidaria_backend.guards.clients;

import com.nexo.manada_solidaria_backend.guards.clients.properties.HolidaysProperties;
import com.nexo.manada_solidaria_backend.guards.clients.responses.HolidayResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class HolidaysRestClient {

    private final RestTemplate restTemplate;
    private final HolidaysProperties holidaysProperties;

    public List<HolidayResponse> getHolidays(int year) {
        HolidayResponse[] holidays = restTemplate.getForEntity(
                holidaysProperties.paths().byYear(),
                HolidayResponse[].class,
                year
        ).getBody();

        return holidays == null ? List.of() : Arrays.asList(holidays);
    }
}
