package com.nexo.manada_solidaria_backend.locations.controllers.responses;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;

import java.util.UUID;

public record LocationResponse(
        UUID id,
        String name,
        String address,
        Integer number,
        Double latitude,
        Double longitude
) {

    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getNumber(),
                location.getLatitude(),
                location.getLongitude()
        );
    }

}