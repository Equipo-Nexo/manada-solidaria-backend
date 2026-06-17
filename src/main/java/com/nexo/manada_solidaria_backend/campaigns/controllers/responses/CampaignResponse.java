package com.nexo.manada_solidaria_backend.campaigns.controllers.responses;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationCampaign;
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
        DonationCampaign donation = campaign instanceof DonationCampaign
                ? (DonationCampaign) campaign
                : null;

        return new CampaignResponse(
                campaign.getId(),
                getType(campaign),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                LocationResponse.from(campaign.getLocation()),
                campaign.getCurrentStatus().getStatus().name(),
                campaign.getCreatedAt(),
                Optional.ofNullable(campaign.getOwner())
                        .map(User::getId)
                        .orElse(null),
                donation != null ? donation.getAmountToBeCollected() : null,
                donation != null ? donation.getAmountCollected() : null,
                donation != null ? donation.getEndDate() : null
        );
    }

    private static CampaignType getType(Campaign campaign) {
        return campaign instanceof DonationCampaign
                ? CampaignType.DONATION
                : CampaignType.NEWS;
    }

    public record LocationResponse(
            UUID id,
            String name,
            String address,
            int number,
            double latitude,
            double longitude
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
