package com.nexo.manada_solidaria_backend.geolocation.clients;

import com.nexo.manada_solidaria_backend.geolocation.clients.properties.GeoapifyProperties;
import com.nexo.manada_solidaria_backend.geolocation.clients.responses.GeoapifyResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@AllArgsConstructor
public class GeoapifyRestClient {

    private final RestTemplate restTemplate;
    private final GeoapifyProperties geoapifyProperties;

    public GeoapifyResponse getGeocodeAutocomplete(String text, Integer limit, Double longitude, Double latitude) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(geoapifyProperties.paths().geocodeAutocomplete())
                .queryParam("apiKey", geoapifyProperties.apiKey())
                .queryParam("text", text)
                .queryParam("limit", limit);

        if (longitude != null && latitude != null) {
            uriBuilder.queryParam(
                    "bias",
                    "proximity:%s,%s".formatted(longitude, latitude)
            );
        }

        return restTemplate.getForEntity(
                uriBuilder.build().encode().toUri(),
                GeoapifyResponse.class
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
