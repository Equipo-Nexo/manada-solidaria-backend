package com.nexo.manada_solidaria_backend.campaigns.controllers.requests;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.locations.controllers.requests.UpdateLocationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateCampaignRequest(

        @NotBlank
        @Size(max = 50)
        String title,

        @NotBlank
        @Size(max = 255)
        String description,

        String imageId,

        @NotBlank
        @Pattern(
                regexp = "^[0-9]{8,15}$",
                message = "El teléfono debe contener entre 8 y 15 dígitos"
        )
        String phoneNumber,

        @Valid
        @NotNull
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
){}