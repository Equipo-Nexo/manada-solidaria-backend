package com.nexo.manada_solidaria_backend.animal_post.controllers.requests;

import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_post.data.enums.AnimalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateAnimalPostRequest(
        @NotNull(message = "El tipo de publicación es obligatorio (PERDIDO o ADOPCION)")
        AnimalPostType type,

        @NotBlank(message = "El título es obligatorio")
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotBlank(message = "El ID de imagen de Cloudflare es obligatorio")
        String imageId,

        // Obligatorio solo cuando type=LOST .
        Boolean hasOwner,

        @NotNull(message = "Los datos del animal son obligatorios")
        @Valid
        AnimalRequest animal,

        @NotNull(message = "Los datos de ubicación son obligatorios")
        @Valid
        LocationRequest location
) {


    @AssertTrue(message = "Tiene dueño es obligatorio cuando el tipo de publicación es PERDIDO")
    public boolean isHasOwnerPresentForLostPost() {
        return type != AnimalPostType.LOST || hasOwner != null;
    }

    public record AnimalRequest(
            @NotNull(message = "El tipo de animal es obligatorio")
            AnimalType type,

            @NotNull(message = "El tamaño del animal es obligatorio")
            AnimalSize size,

            @NotNull(message = "El género del animal es obligatorio")
            AnimalGender gender,

            String color,
            String breed,
            String fur,
            String age,
            String description
    ) {
    }

    public record LocationRequest(
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