package com.nexo.manada_solidaria_backend.geolocation.clients.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients.rest.apis.geoapify")
public record GeoapifyProperties(
        String apiKey,
        String host,
        Paths paths
) {
    public record Paths(
            String geocodeAutocomplete,
            String geocodeReverse
    ) {}
}