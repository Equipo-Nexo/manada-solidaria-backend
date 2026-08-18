package com.nexo.manada_solidaria_backend.geolocation.clients.responses;

import java.util.List;

public record GeoapifyResponse(
        List<Feature> features
) {
    public record Feature(
        Property properties
    ) {}

    public record Property(
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
    ){}
}
