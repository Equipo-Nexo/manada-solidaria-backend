package com.nexo.manada_solidaria_backend.locations.controllers.requests;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
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
}