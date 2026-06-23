package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalAge;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.common.controllers.validations.ConditionalField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;


@ConditionalField(
        field = "hasOwner",
        dependsOn = "type",
        expectedValue = "LOST",
        message = "El parámetro hasOwner es obligatorio cuando el tipo de publicación es LOST"
)
@ConditionalField(
        field = "inTransit",
        dependsOn = "type",
        expectedValue = "ADOPTION",
        message = "El parámetro inTransit es obligatorio cuando el tipo de publicación es ADOPTION"
)
@ConditionalField(
        field = "reward",
        dependsOn = "type",
        expectedValue = "LOST",
        rule = ConditionalField.Rule.ONLY_ALLOWED,
        message = "La recompensa solo aplica a publicaciones de tipo LOST"
)
public record CreateAnimalPostRequest(
        @NotNull(message = "El tipo de publicación es obligatorio (LOST o ADOPTION)")
        AnimalPostType type,
        //definir tamaño maximo.
        @NotBlank(message = "El título es obligatorio")
        String title,
        //definir tamaño maximo.
        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotBlank(message = "El ID de imagen de Cloudflare es obligatorio")
        String imageId,

        @NotBlank(message = "El número de teléfono es obligatorio")
        String phoneNumber,

        Boolean hasOwner,

        Boolean inTransit,

        @PositiveOrZero(message = "La recompensa no puede ser negativa")
        BigDecimal reward,

        @NotNull(message = "Los datos del animal son obligatorios")
        @Valid
        AnimalRequest animal,

        @NotNull(message = "Los datos de ubicación son obligatorios")
        @Valid
        LocationRequest location
) {

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
            @NotNull(message = "La edad del animal es obligatoria")
            AnimalAge age,
            String description
    ) {
    }

    //DEFINIR VALIDACIONES PARA LOS CAMPOS DE UBICACION (LATITUD, LONGITUD, ETC)
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