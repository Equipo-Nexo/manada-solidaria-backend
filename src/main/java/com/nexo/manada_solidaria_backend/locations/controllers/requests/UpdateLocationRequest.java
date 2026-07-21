package com.nexo.manada_solidaria_backend.locations.controllers.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateLocationRequest(

        @NotBlank(message = "El nombre de la ubicación es obligatorio")
        String name,

        @NotBlank(message = "La dirección es obligatoria")
        String address,

        int number,

        double latitude,

        double longitude
) {
}