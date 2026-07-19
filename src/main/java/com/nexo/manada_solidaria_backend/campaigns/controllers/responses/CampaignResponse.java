package com.nexo.manada_solidaria_backend.campaigns.controllers.responses;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.FundraisingCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
        String accountAlias,
        Long amountToBeCollected,
        Long amountCollected,
        LocalDate campaignEndDate,
        List<DonationItemResponse> items,
        LocalDateTime newsStartDateTime,
        LocalDateTime newsEndDateTime,
        String newsCategory
) {

    public static CampaignResponse from(Campaign campaign) {
        return switch (campaign) {
            case FundraisingCampaign donation -> buildFundraisingCampaignResponse(donation);
            case NewsCampaign news -> buildNewsCampaignResponse(news);
            case DonationCampaign donation -> buildDonationCampaignResponse(donation);
            default -> throw new IllegalArgumentException("Unsupported campaign type");
        };
    }

    private static CampaignResponse buildFundraisingCampaignResponse(FundraisingCampaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                CampaignType.FUNDRAISING,
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                LocationResponse.from(campaign.getLocation()),
                campaign.getCurrentStatus().getStatus().name(),
                campaign.getCreatedAt(),
                Optional.ofNullable(campaign.getOwner())
                        .map(User::getId)
                        .orElse(null),
                campaign.getAccountAlias(),
                campaign.getAmountToBeCollected(),
                campaign.getAmountCollected(),
                campaign.getCampaignEndDate(),
                null,
                null,
                null,
                null
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
                null,
                null,
                null,
                campaign.getNewsStartDateTime(),
                campaign.getNewsEndDateTime(),
                campaign.getCategory().name()
        );
    }

    private static CampaignResponse buildDonationCampaignResponse(DonationCampaign campaign) {
        List<DonationItemResponse> itemResponses = campaign.getItems().stream()
                .map(item -> new DonationItemResponse(item.getId(), item.getName(), item.isCompleted(), item.getCategory().name()))
                .toList();

        return new CampaignResponse(
                campaign.getId(),
                CampaignType.DONATION,
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                LocationResponse.from(campaign.getLocation()),
                campaign.getCurrentStatus().getStatus().name(),
                campaign.getCreatedAt(),
                Optional.ofNullable(campaign.getOwner()).map(User::getId).orElse(null),
                null,
                null,
                null,
                campaign.getCampaignEndDate(),
                itemResponses,
                null,
                null,
                null
        );
    }

    public record DonationItemResponse(
            UUID id,
            String name,
            boolean isCompleted,
            String category
    ) {}

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
