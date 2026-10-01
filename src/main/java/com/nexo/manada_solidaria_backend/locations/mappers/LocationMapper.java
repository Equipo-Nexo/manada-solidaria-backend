package com.nexo.manada_solidaria_backend.locations.mappers;

import com.nexo.manada_solidaria_backend.locations.controllers.requests.LocationRequest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(LocationRequest request) {
        if (request == null) {
            return null;
        }

        return new Location(
                request.country(),
                request.city(),
                request.formatted(),
                request.district(),
                request.street(),
                request.houseNumber(),
                request.latitude(),
                request.longitude()
        );
    }
}