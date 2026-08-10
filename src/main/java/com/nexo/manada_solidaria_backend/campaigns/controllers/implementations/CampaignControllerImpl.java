package com.nexo.manada_solidaria_backend.campaigns.controllers.implementations;

import com.nexo.manada_solidaria_backend.campaigns.controllers.interfaces.CampaignController;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignCategoryFilter;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class CampaignControllerImpl implements CampaignController {

    private final CampaignService campaignService;

    @Override
    public CampaignResponse create(CreateCampaignRequest request, User owner) {
        return campaignService.create(request, owner);
    }

    @Override
    public Page<CampaignResponse> getCampaigns(CampaignCategoryFilter category, Pageable pageable) {
        return campaignService.getCampaigns(category, pageable);
    }

    @Override
    public CampaignResponse getCampaign(UUID campaignId) {
        return campaignService.getCampaign(campaignId);
    }

    @Override
    public Page<CampaignResponse> getFundraisingCampaigns(Pageable pageable) {
        return campaignService.getFundraisingCampaigns(pageable);
    }

    @Override
    public void update(UUID campaignId, UpdateCampaignRequest request, User authenticatedUser) {
        campaignService.update(campaignId, request, authenticatedUser);
    }

    @Override
    public void delete(UUID campaignId, User authenticatedUser) {
        campaignService.delete(campaignId, authenticatedUser);
    }
}
