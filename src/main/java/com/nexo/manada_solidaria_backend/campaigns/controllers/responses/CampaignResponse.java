package com.nexo.manada_solidaria_backend.campaigns.controllers.responses;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.FundraisingCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign;
import com.nexo.manada_solidaria_backend.common.controllers.responses.LocationResponse;
import com.nexo.manada_solidaria_backend.common.controllers.responses.PhoneNumberResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record CampaignResponse(
        UUID id,
        String type,
        String title,
        String description,
        String imageId,
        PhoneNumberResponse phoneNumber,
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
        LocalDateTime newsEndDateTime
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
                CampaignType.FUNDRAISING.getValue(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                PhoneNumberResponse.from(campaign.getPhoneNumber()),
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
                null
        );
    }

    private static CampaignResponse buildNewsCampaignResponse(NewsCampaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getCategory().getValue(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                PhoneNumberResponse.from(campaign.getPhoneNumber()),
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
                campaign.getNewsEndDateTime()
        );
    }

    private static CampaignResponse buildDonationCampaignResponse(DonationCampaign campaign) {
        List<DonationItemResponse> itemResponses = campaign.getItems().stream()
                .map(item -> new DonationItemResponse(item.getId(), item.getName(), item.isCompleted(), item.getCategory().name()))
                .toList();

        return new CampaignResponse(
                campaign.getId(),
                CampaignType.DONATION.getValue(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getImageId(),
                PhoneNumberResponse.from(campaign.getPhoneNumber()),
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
                null
        );
    }

    public record DonationItemResponse(
            UUID id,
            String name,
            boolean isCompleted,
            String category
    ) {}
}
