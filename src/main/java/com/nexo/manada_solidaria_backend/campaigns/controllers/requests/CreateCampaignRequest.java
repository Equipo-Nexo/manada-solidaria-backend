package com.nexo.manada_solidaria_backend.campaigns.controllers.requests;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.validations.CampaignTypeValidation;
import com.nexo.manada_solidaria_backend.common.controllers.validations.ConditionalField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@ConditionalField(
        field = "amountToBeCollected",
        dependsOn = "type",
        expectedValue = "DONATION",
        rule = ConditionalField.Rule.ONLY_ALLOWED,
        message = "El monto a recaudar solo aplica para campañas de tipo DONATION"
)
@ConditionalField(
        field = "campaignEndDate",
        dependsOn = "type",
        expectedValue = "DONATION",
        rule = ConditionalField.Rule.ONLY_ALLOWED,
        message = "La fecha de finalización solo aplica para campañas de tipo DONATION"
)
public record CreateCampaignRequest(

        @NotNull(message = "El tipo de campaña es obligatorio")
        CampaignType type,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100)
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255)
        String description,

        String imageId,

        @NotNull(message = "La ubicación es obligatoria")
        @Valid
        LocationRequest location,

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