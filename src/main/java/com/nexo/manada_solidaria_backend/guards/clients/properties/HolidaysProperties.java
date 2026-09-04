package com.nexo.manada_solidaria_backend.guards.clients.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients.rest.apis.holidays")
public record HolidaysProperties(
        Paths paths
) {
    public record Paths(
            String byYear
    ) {}
}
