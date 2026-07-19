package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.components.CampaignFactory;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignCategoryFilter;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Page<CampaignResponse> getCampaigns(CampaignCategoryFilter category, Pageable pageable) {
        Page<Campaign<?>> campaigns = switch (category) {
            case null -> campaignRepository.findCampaigns(pageable);
            case DONATION -> campaignRepository.findDonationCampaigns(pageable);
            default -> campaignRepository.findNewsCampaignsByCategory(
                    NewsCampaignCategory.valueOf(category.name()),
                    pageable
            );
        };

        return toResponse(campaigns);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignResponse> getFundraisingCampaigns(Pageable pageable) {
        return toResponse(campaignRepository.findFundraisingCampaigns(pageable));
    }

    private <T extends Campaign<?>> Page<CampaignResponse> toResponse(Page<T> campaigns) {
        return campaigns.map(CampaignResponse::from);
    }

}