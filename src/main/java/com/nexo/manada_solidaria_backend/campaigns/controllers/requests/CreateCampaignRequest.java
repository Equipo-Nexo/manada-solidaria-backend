package com.nexo.manada_solidaria_backend.campaigns.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.validations.ConditionalField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@ConditionalField(
        field = "accountAlias",
        dependsOn = "type",
        expectedValue = "FUNDRAISING",
        rule = ConditionalField.Rule.REQUIRED,
        message = "El alias es obligatorio solo para campañas de tipo FUNDRAISING"
)
@ConditionalField(
        field = "amountToBeCollected",
        dependsOn = "type",
        expectedValue = "FUNDRAISING",
        rule = ConditionalField.Rule.ONLY_ALLOWED,
        message = "El monto a recaudar solo aplica para campañas de tipo FUNDRAISING"
)
@ConditionalField(
        field = "campaignEndDate",
        dependsOn = "type",
        expectedValue = "FUNDRAISING",
        rule = ConditionalField.Rule.ONLY_ALLOWED,
        message = "La fecha de finalización solo aplica para campañas de tipo FUNDRAISING"
)
public record CreateCampaignRequest(

        @NotNull(message = "El tipo de campaña es obligatorio")
        CampaignType type,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 50)
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255)
        String description,

        String imageId,

        @NotNull(message = "La ubicación es obligatoria")
        @Valid
        LocationRequest location,

        @Size(min = 6, max = 20, message = "El alias debe tener entre 6 y 20 caracteres.")
        @Pattern(regexp = "^[a-zA-Z0-9.-]+$", message = "El alias solo puede contener letras, números, puntos o guiones.")
        String accountAlias,

        @Positive(message = "El monto a recaudar debe ser mayor a 0")
        Long amountToBeCollected,

        @FutureOrPresent(message = "La fecha de finalización debe ser posterior o igual al día de hoy")
        LocalDate campaignEndDate

) {

    public record LocationRequest(
            @NotBlank(message = "El nombre de la ubicacion es obligatorio.")
            String name,

            String address,

            Integer number,

            @NotNull(message = "La latitud es obligatoria")
            Double latitude,

            @NotNull(message = "La longitud es obligatoria")
            Double longitude
    ) {
    }
}