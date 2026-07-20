package com.nexo.manada_solidaria_backend.campaigns.controllers.requests;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.common.controllers.validations.ConditionalField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ConditionalField(
        field = "accountAlias",
        dependsOn = "type",
        expectedValue = "FUNDRAISING",
        rule = ConditionalField.Rule.REQUIRED_AND_ONLY_ALLOWED,
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
        expectedValue = "NEWS",
        rule = ConditionalField.Rule.NOT_ALLOWED,
        message = "La fecha de finalización no aplica para campañas NEWS"
)
@ConditionalField(
        field = "items",
        dependsOn = "type",
        expectedValue = "DONATION",
        rule = ConditionalField.Rule.REQUIRED_AND_ONLY_ALLOWED,
        message = "La lista de items es obligatorio solo para campañas de tipo DONATION"
)
@ConditionalField(
        field = "newsStartDateTime",
        dependsOn = "type",
        expectedValue = "NEWS",
        rule = ConditionalField.Rule.ONLY_ALLOWED,
        message = "La fecha de inicio solo aplica para campañas NEWS"
)
@ConditionalField(
        field = "newsEndDateTime",
        dependsOn = "type",
        expectedValue = "NEWS",
        rule = ConditionalField.Rule.REQUIRED_AND_ONLY_ALLOWED,
        message = "La fecha de fin es obligatoria solo para campañas de tipo NEWS"
)
@ConditionalField(
        field = "category",
        dependsOn = "type",
        expectedValue = "NEWS",
        rule = ConditionalField.Rule.REQUIRED_AND_ONLY_ALLOWED,
        message = "La categoría es obligatoria solo para campañas de tipo NEWS"
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
        LocalDate campaignEndDate,

        @Valid
        @Size(min = 1, message = "Debe haber al menos un ítem")
        List<DonationItemRequest> items,

        LocalDateTime newsStartDateTime,

        @Future(message = "La fecha de finalización debe ser futura")
        LocalDateTime newsEndDateTime,

        NewsCampaignCategory category

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
    ) {}

    public record DonationItemRequest(
            String name,

            @NotNull(message = "La categoría es obligatoria")
            DonationCampaignCategory category
    ) {}
}