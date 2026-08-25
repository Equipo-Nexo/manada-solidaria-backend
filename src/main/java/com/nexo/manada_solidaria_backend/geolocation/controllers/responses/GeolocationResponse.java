package com.nexo.manada_solidaria_backend.geolocation.controllers.responses;

public record GeolocationResponse(
        String country,
        String state,
        String city,
        String municipality,
        String district,
        String street,
        String housenumber,
        double lon,
        double lat,
        String result_type,
        String formatted,
        String address_line1,
        String address_line2
) {
}
