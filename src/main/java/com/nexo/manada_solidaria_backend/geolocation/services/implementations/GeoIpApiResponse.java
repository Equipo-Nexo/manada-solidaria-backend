package com.nexo.manada_solidaria_backend.geolocation.services.implementations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record GeoIpApiResponse(
        Double latitude,
        Double longitude,
        String city,
        String stateprov,
        String country
) {
}
