package com.nexo.manada_solidaria_backend.campaigns.controllers.requests;

import jakarta.validation.constraints.NotBlank;

public record TransitionCampaignStatusRequest(
        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}