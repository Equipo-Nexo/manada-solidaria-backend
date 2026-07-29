package com.nexo.manada_solidaria_backend.geolocation.controllers.responses;

public record GeolocationResponse(
        Double latitude,
        Double longitude,
        String city,
        String region,
        String country
) {
}
