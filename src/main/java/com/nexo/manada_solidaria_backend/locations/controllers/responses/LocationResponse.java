package com.nexo.manada_solidaria_backend.locations.controllers.responses;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;

import java.util.Optional;
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
        return Optional.ofNullable(location)
                .map(loc -> new LocationResponse(
                        loc.getId(),
                        loc.getName(),
                        loc.getAddress(),
                        loc.getNumber(),
                        loc.getLatitude(),
                        loc.getLongitude()
                ))
                .orElse(null);
    }

}