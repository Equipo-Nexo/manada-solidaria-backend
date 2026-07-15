package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignUserPostResponse extends UserPostResponse {
    public CampaignUserPostResponse(CampaignResponse campaignResponse) {
        super(campaignResponse.id(), campaignResponse.title(), campaignResponse.createdAt(), "campaign");
    }
}
