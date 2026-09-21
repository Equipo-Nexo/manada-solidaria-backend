package com.nexo.manada_solidaria_backend.common.controllers.responses;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;

import java.util.Optional;
import java.util.UUID;

public record LocationResponse(
        UUID id,
        String country,
        String city,
        String formatted,
        String district,
        String street,
        Integer houseNumber,
        Double latitude,
        Double longitude
) {

    public static LocationResponse from(Location location) {
        return Optional.ofNullable(location)
                .map(loc -> new LocationResponse(
                        loc.getId(),
                        loc.getCountry(),
                        loc.getCity(),
                        loc.getFormatted(),
                        loc.getDistrict(),
                        loc.getStreet(),
                        loc.getHouseNumber(),
                        loc.getLatitude(),
                        loc.getLongitude()
                ))
                .orElse(null);
    }
}