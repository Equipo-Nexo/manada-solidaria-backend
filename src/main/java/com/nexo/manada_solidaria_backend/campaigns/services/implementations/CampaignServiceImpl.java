package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.components.CampaignFactory;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignCategoryFilter;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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

    @Override
    @Transactional
    public void update(UUID campaignId, UpdateCampaignRequest request, User authenticatedUser) {
        Campaign campaign = getOwnedCampaignOrThrow(campaignId, authenticatedUser);

        campaign.update(request);
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public void delete(UUID campaignId, User authenticatedUser) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La campaña no existe"));

        if (!campaign.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el dueño puede eliminar la campaña");
        }

        if (campaign.isFinished()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar una campaña finalizada");
        }

        campaignRepository.delete(campaign);
    }

    @Override
    public List<CampaignResponse> getUserCampaigns(User user) {
        return campaignRepository
                .findAllByOwner(user)
                .stream()
                .map(CampaignResponse::from)
                .toList();
    }

    private <T extends Campaign<?>> Page<CampaignResponse> toResponse(Page<T> campaigns) {
        return campaigns.map(CampaignResponse::from);
    }

    private Campaign getOwnedCampaignOrThrow(UUID campaignId, User authenticatedUser) {
        Campaign campaign = getCampaignOrThrow(campaignId);

        validateOwner(campaign, authenticatedUser);
        return campaign;
    }

    private Campaign getCampaignOrThrow(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La campaña no existe"
                        )
                );
    }

    private void validateOwner(Campaign campaign, User authenticatedUser) {
        if (!campaign.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el dueño puede editar la campaña"
            );
        }
    }

}