package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalAge;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateAnimalPostRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotBlank(message = "El ID de imagen de Cloudflare es obligatorio")
        String imageId,

        // Opcional: un post "en la calle" (LOST sin dueño) no tiene teléfono y debe poder editarse.
        String phoneNumber,

        @PositiveOrZero(message = "La recompensa no puede ser negativa")
        BigDecimal reward,

        @NotNull(message = "Los datos del animal son obligatorios")
        @Valid
        AnimalUpdate animal,

        @NotNull(message = "Los datos de ubicación son obligatorios")
        @Valid
        LocationUpdate location
) {

    public record AnimalUpdate(
            @NotNull(message = "El tipo de animal es obligatorio")
            AnimalType type,

            @NotNull(message = "El tamaño del animal es obligatorio")
            AnimalSize size,

            @NotNull(message = "El género del animal es obligatorio")
            AnimalGender gender,

            String color,

            @NotNull(message = "La edad del animal es obligatoria")
            AnimalAge age
    ) {
    }

    public record LocationUpdate(
            @NotBlank(message = "El nombre de la ubicación es obligatorio")
            String name,

            @NotBlank(message = "La dirección es obligatoria")
            String address,

            int number,
            double latitude,
            double longitude
    ) {
    }
}
