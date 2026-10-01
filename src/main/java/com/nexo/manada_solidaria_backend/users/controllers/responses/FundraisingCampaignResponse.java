package com.nexo.manada_solidaria_backend.users.controllers.responses;

import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundraisingCampaignResponse extends CampaignUserPostResponse {

    public FundraisingCampaignResponse(CampaignResponse campaignResponse) {
        super(campaignResponse, FUNDRAISING);
    }
}
