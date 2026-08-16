package com.nexo.manada_solidaria_backend.geolocation.clients;

import com.nexo.manada_solidaria_backend.geolocation.clients.properties.GeoapifyProperties;
import com.nexo.manada_solidaria_backend.geolocation.clients.responses.GeoapifyResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class GeoapifyRestClient {

    private final RestTemplate restTemplate;
    private final GeoapifyProperties geoapifyProperties;

    public GeoapifyResponse getGeocodeAutocomplete(String text, int limit) {
        return restTemplate.getForEntity(
                geoapifyProperties.paths().geocodeAutocomplete(),
                GeoapifyResponse.class,
                geoapifyProperties.apiKey(),
                text,
                limit
        ).getBody();
    }

    public GeoapifyResponse getGeocodeReverse(double latitude, double longitude) {
        return restTemplate.getForEntity(
                geoapifyProperties.paths().geocodeReverse(),
                GeoapifyResponse.class,
                geoapifyProperties.apiKey(),
                latitude,
                longitude
        ).getBody();
    }

}
