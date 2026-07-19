package com.nexo.manada_solidaria_backend.campaigns.services.interfaces;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignCategoryFilter;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignService {

    CampaignResponse create(CreateCampaignRequest request, User owner);

    Page<CampaignResponse> getCampaigns(CampaignCategoryFilter category, Pageable pageable);

    Page<CampaignResponse> getFundraisingCampaigns(Pageable pageable);
}