package com.nexo.manada_solidaria_backend.campaigns.services.interfaces;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface CampaignService {

    CampaignResponse create(CreateCampaignRequest request, User owner);
}