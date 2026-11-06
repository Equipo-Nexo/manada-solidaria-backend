package com.nexo.manada_solidaria_backend.events.data.models;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;

import java.util.UUID;

public record NewCampaignCreatedEvent(
        CampaignType type,
        String ownerUsername,
        UUID campaignId,
        UUID ownerId
) {
}
