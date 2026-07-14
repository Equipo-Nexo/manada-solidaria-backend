package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.components.CampaignFactory;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
    public Page<CampaignResponse> getCampaigns(CampaignType type, Pageable pageable) {
        return campaignRepository.findAllFiltered(getSafeValue(type), pageable)
                .map(CampaignResponse::from);
    }

    private String getSafeValue(CampaignType type) {
        return (type != null) ? type.name() : null;
    }

    @Override
    @Transactional
    public void delete(UUID campaignId, User authenticatedUser) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La campaña no existe"));

        if (!campaign.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el dueño puede eliminar la campaña");
        }

        if (isFinished(campaign)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar una campaña finalizada");
        }

        campaignRepository.delete(campaign);
    }

    private boolean isFinished(Campaign campaign) {
        return campaign.getCurrentStatus().getStatus().name().equals("FINISHED");
    }
}