package com.nexo.manada_solidaria_backend.campaigns.controllers.responses;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record CampaignResponse(
        UUID id,
        CampaignType type,
        String title,
        String description,
        String imageId,
        LocationResponse location,
        String status,
        LocalDateTime createdAt,
        UUID ownerId,
        Long amountToBeCollected,
        Long amountCollected,
        LocalDate campaignEndDate
) {

    public static CampaignResponse from(Campaign campaign) {
        return switch (campaign) {
            case DonationCampaign donation -> buildDonationCampaignResponse(donation);
            case NewsCampaign news -> buildNewsCampaignResponse(news);
            default -> throw new IllegalArgumentException("Unsupported campaign type");
        };
    }

    private static CampaignResponse buildDonationCampaignResponse(DonationCampaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                CampaignType.DONATION,
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                LocationResponse.from(campaign.getLocation()),
                campaign.getCurrentStatus().getStatus().name(),
                campaign.getCreatedAt(),
                Optional.ofNullable(campaign.getOwner())
                        .map(User::getId)
                        .orElse(null),
                campaign.getAmountToBeCollected(),
                campaign.getAmountCollected(),
                campaign.getEndDate()
        );
    }

    private static CampaignResponse buildNewsCampaignResponse(NewsCampaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                CampaignType.NEWS,
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                LocationResponse.from(campaign.getLocation()),
                campaign.getCurrentStatus().getStatus().name(),
                campaign.getCreatedAt(),
                Optional.ofNullable(campaign.getOwner())
                        .map(User::getId)
                        .orElse(null),
                null,
                null,
                null
        );
    }

    public record LocationResponse(
            UUID id,
            String name,
            String address,
            Integer number,
            Double latitude,
            Double longitude
    ) {

        static LocationResponse from(Location location) {
            return new LocationResponse(
                    location.getId(),
                    location.getName(),
                    location.getAddress(),
                    location.getNumber(),
                    location.getLatitude(),
                    location.getLongitude()
            );
        }
    }
}
