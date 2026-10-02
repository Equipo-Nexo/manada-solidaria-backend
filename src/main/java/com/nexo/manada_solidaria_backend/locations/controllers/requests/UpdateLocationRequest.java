package com.nexo.manada_solidaria_backend.locations.controllers.requests;

import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateLocationRequest(

        @NotBlank(message = "El país es obligatorio")
        String country,

        @NotBlank(message = "La ciudad es obligatoria")
        String city,

        @NotBlank(message = "El formato de dirección es obligatorio")
        String formatted,

        String district,
        String street,
        Integer houseNumber,
        Double latitude,
        Double longitude
) {
        public Location toDomain() {
                return new Location(
                        this.country,
                        this.city,
                        this.formatted,
                        this.district,
                        this.street,
                        this.houseNumber,
                        this.latitude,
                        this.longitude
                );
        }
}