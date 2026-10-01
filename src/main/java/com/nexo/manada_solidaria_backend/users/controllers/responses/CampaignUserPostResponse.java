package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse.DonationItemResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CampaignUserPostResponse extends UserPostResponse {

    private final String accountAlias;
    private final Long amountToBeCollected;
    private final Long amountCollected;
    private final LocalDate campaignEndDate;
    private final List<DonationItemResponse> items;
    private final LocalDateTime newsStartDateTime;
    private final LocalDateTime newsEndDateTime;

    public CampaignUserPostResponse(CampaignResponse campaignResponse) {
        this(campaignResponse, CAMPAIGN);
    }

    protected CampaignUserPostResponse(CampaignResponse campaignResponse, String postType) {
        super(
                campaignResponse.id(),
                campaignResponse.title(),
                campaignResponse.description(),
                campaignResponse.createdAt(),
                campaignResponse.imageId(),
                postType,
                campaignResponse.status(),
                campaignResponse.type(),
                campaignResponse.location(),
                campaignResponse.phoneNumber(),
                campaignResponse.ownerId()
        );
        this.accountAlias = campaignResponse.accountAlias();
        this.amountToBeCollected = campaignResponse.amountToBeCollected();
        this.amountCollected = campaignResponse.amountCollected();
        this.campaignEndDate = campaignResponse.campaignEndDate();
        this.items = campaignResponse.items();
        this.newsStartDateTime = campaignResponse.newsStartDateTime();
        this.newsEndDateTime = campaignResponse.newsEndDateTime();
    }
}
