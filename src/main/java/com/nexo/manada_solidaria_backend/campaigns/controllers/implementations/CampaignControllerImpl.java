package com.nexo.manada_solidaria_backend.campaigns.controllers.implementations;

import com.nexo.manada_solidaria_backend.campaigns.controllers.interfaces.CampaignController;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CampaignControllerImpl implements CampaignController {

    private final CampaignService campaignService;

    @Override
    public CampaignResponse create(
            CreateCampaignRequest request,
            User owner
    ) {
        return campaignService.create(
                request,
                owner
        );
    }

    @Override
    public Page<CampaignResponse> getCampaigns(CampaignType type, Pageable pageable) {
        return campaignService.getCampaigns(type, pageable);
    }
}
