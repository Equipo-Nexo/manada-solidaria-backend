package com.nexo.manada_solidaria_backend.campaigns.services.interfaces;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignCategoryFilter;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CampaignService {

    CampaignResponse create(CreateCampaignRequest request, User owner);

    Page<CampaignResponse> getCampaigns(CampaignCategoryFilter category, Pageable pageable);

    CampaignResponse getCampaign(UUID campaignId);

    Page<CampaignResponse> getFundraisingCampaigns(Pageable pageable);

    void update(UUID campaignId, UpdateCampaignRequest request, User authenticatedUser);

    void delete(UUID campaignId, User authenticatedUser);

    List<CampaignResponse> getUserCampaigns(User user);

    List<CampaignResponse> getUserFundraisingCampaigns(User user);

    long countFinishedUserCampaigns(User user);

    long countUserCampaigns(User user);
}