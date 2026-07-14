package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.components.CampaignFactory;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.common.utils.EnumUtils;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignFactory campaignFactory;

    @Override
    public CampaignResponse create(CreateCampaignRequest request, User owner) {
        Campaign saved = campaignRepository.save(campaignFactory.buildCampaign(request, owner));

        return CampaignResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignResponse> getCampaigns(CampaignType type, Pageable pageable) {
        return campaignRepository.findAllFiltered(EnumUtils.getNameOrNull(type), pageable)
                .map(CampaignResponse::from);
    }

    @Override
    public List<CampaignResponse> getUserCampaigns(User user) {
        return campaignRepository
                .findAllByOwner(user)
                .stream()
                .map(CampaignResponse::from)
                .toList();
    }
}