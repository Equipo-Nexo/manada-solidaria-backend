package com.nexo.manada_solidaria_backend.geolocation.utils;

import com.nexo.manada_solidaria_backend.geolocation.clients.responses.GeoapifyResponse;

import java.util.List;

public final class MockGeolocationDataUtils {

    private MockGeolocationDataUtils() {
    }

    public static GeoapifyResponse twoFeaturesResponse() {
        return new GeoapifyResponse(List.of(
                feature(
                        "Ciudad Autonoma de Buenos Aires",
                        "Buenos Aires",
                        "Comuna 1",
                        "San Nicolas",
                        "Avenida Corrientes",
                        "100",
                        -58.3816,
                        -34.6037,
                        "amenity",
                        "Avenida Corrientes 100, Buenos Aires",
                        "Avenida Corrientes 100",
                        "Buenos Aires, Argentina"
                ),
                feature(
                        "Buenos Aires",
                        "La Plata",
                        "La Plata",
                        "Casco Urbano",
                        "Calle 50",
                        "1",
                        -57.9545,
                        -34.9214,
                        "street",
                        "Calle 50 1, La Plata",
                        "Calle 50 1",
                        "La Plata, Argentina"
                )
        ));
    }

    public static GeoapifyResponse emptyResponse() {
        return new GeoapifyResponse(List.of());
    }

    private static GeoapifyResponse.Feature feature(
            String state,
            String city,
            String municipality,
            String district,
            String street,
            String houseNumber,
            double longitude,
            double latitude,
            String resultType,
            String formatted,
            String addressLine1,
            String addressLine2
    ) {
        return new GeoapifyResponse.Feature(
                new GeoapifyResponse.Property(
                        "Argentina",
                        state,
                        city,
                        municipality,
                        district,
                        street,
                        houseNumber,
                        longitude,
                        latitude,
                        resultType,
                        formatted,
                        addressLine1,
                        addressLine2
                )
        );
    }
}
