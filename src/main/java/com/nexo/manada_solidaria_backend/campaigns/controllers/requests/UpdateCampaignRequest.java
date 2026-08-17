package com.nexo.manada_solidaria_backend.campaigns.controllers.requests;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.common.controllers.validations.ConditionalField;
import com.nexo.manada_solidaria_backend.locations.controllers.requests.UpdateLocationRequest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ConditionalField(
        field = "location",
        dependsOn = "type",
        expectedValue = "DONATION",
        rule = ConditionalField.Rule.REQUIRED,
        message = "La ubicación es obligatoria para campañas de tipo DONATION"
)
@ConditionalField(
        field = "location",
        dependsOn = "type",
        expectedValue = "NEWS",
        rule = ConditionalField.Rule.REQUIRED,
        message = "La ubicación es obligatoria para campañas de tipo NEWS"
)
public record UpdateCampaignRequest(

        @NotNull(message = "El tipo de campaña es obligatorio")
        CampaignType type,

        @NotBlank
        @Size(max = 50)
        String title,

        @NotBlank
        @Size(max = 255)
        String description,

        String imageId,

        @NotNull(message = "El teléfono es obligatorio")
        @Valid
        PhoneNumberRequest phoneNumber,

        @Valid
        UpdateLocationRequest location,

        @Size(min = 6, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9.-]+$")
        String accountAlias,

        @Positive
        Long amountToBeCollected,

        @PositiveOrZero
        Long amountCollected,

        @FutureOrPresent
        LocalDate campaignEndDate,

        LocalDateTime newsStartDateTime,

        @Future
        LocalDateTime newsEndDateTime,

        NewsCampaignCategory category
){ }