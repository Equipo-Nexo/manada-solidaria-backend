package com.nexo.manada_solidaria_backend.campaigns.services.interfaces;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

import java.util.List;

public interface CampaignService {

    CampaignResponse create(CreateCampaignRequest request, User owner);

    Page<CampaignResponse> getCampaigns(CampaignType type, Pageable pageable);

    void delete(UUID campaignId, User authenticatedUser);

    List<CampaignResponse> getUserCampaigns(User user);
}