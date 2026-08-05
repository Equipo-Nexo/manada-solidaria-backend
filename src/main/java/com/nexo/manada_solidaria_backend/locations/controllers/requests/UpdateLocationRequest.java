package com.nexo.manada_solidaria_backend.locations.controllers.requests;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateLocationRequest(

        @NotBlank(message = "El nombre de la ubicación es obligatorio")
        String name,

        String address,

        Integer number,

        @NotNull(message = "La latitud es obligatoria")
        Double latitude,

        @NotNull(message = "La longitud es obligatoria")
        Double longitude
) {

        public Location toDomain() {
                return new Location(
                        this.name,
                        this.address,
                        this.number,
                        this.latitude,
                        this.longitude
                );
        }
}