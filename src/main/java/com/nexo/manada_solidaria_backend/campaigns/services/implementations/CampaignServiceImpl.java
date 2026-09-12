package com.nexo.manada_solidaria_backend.campaigns.services.implementations;

import com.nexo.manada_solidaria_backend.campaigns.components.CampaignFactory;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.TransitionCampaignStatusRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.responses.CampaignResponse;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignCategoryFilter;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignFactory campaignFactory;

    @Override
    public CampaignResponse create(CreateCampaignRequest request, User owner) {
        Campaign<?, ?> saved = campaignRepository.save(campaignFactory.buildCampaign(request, owner));

        log.info("Campaign created: id={} type={} title='{}' owner={}",
                saved.getId(), saved.getCampaignType(), saved.getTitle(), owner.getId());

        return CampaignResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignResponse> getCampaigns(
            CampaignCategoryFilter category,
            Pageable pageable
    ) {
        log.debug("Listing campaigns: category={} page={}", category, pageable);
        Page<Campaign<?, ?>> campaigns = switch (category) {
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
    public CampaignResponse getCampaign(UUID campaignId) {
        Campaign<?, ?> campaign = getCampaignOrThrowException(campaignId);

        return CampaignResponse.from(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignResponse> getFundraisingCampaigns(Pageable pageable) {
        log.debug("Listing fundraising campaigns: page={}", pageable);

        return toResponse(campaignRepository.findFundraisingCampaigns(pageable));
    }

    @Override
    @Transactional
    public void update(UUID campaignId, UpdateCampaignRequest request, User authenticatedUser) {
        Campaign<?, ?> campaign = getOwnedCampaignOrThrow(campaignId, authenticatedUser);

        validateCampaignType(campaign, request.type());
        campaign.update(request);
        campaignRepository.save(campaign);

        log.info("Campaign updated: id={} by={}", campaignId, authenticatedUser.getId());
    }

    @Override
    @Transactional
    public void delete(UUID campaignId, User authenticatedUser) {
        Campaign<?, ?> campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La campaña no existe"));

        if (!campaign.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el dueño puede eliminar la campaña");
        }

        if (campaign.isFinished()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar una campaña finalizada");
        }

        campaignRepository.delete(campaign);
        log.info("Campaign deleted: id={} by={}", campaignId, authenticatedUser.getId());
    }

    @Override
    public List<CampaignResponse> getUserCampaigns(User user) {
        log.debug("Listing campaigns for user: userId={}", user.getId());

        return campaignRepository
                .findCampaignsByOwner(user)
                .stream()
                .map(CampaignResponse::from)
                .toList();
    }

    @Override
    public List<CampaignResponse> getUserFundraisingCampaigns(User user) {
        log.debug("Listing fundraising campaigns for user: userId={}", user.getId());

        return campaignRepository
                .findFundraisingCampaignsByOwner(user)
                .stream()
                .map(CampaignResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public CampaignResponse transitionStatus(UUID campaignId, TransitionCampaignStatusRequest request, User authenticatedUser) {
        Campaign<?, ?> campaign = getOwnedCampaignOrThrow(campaignId, authenticatedUser);
        String previousStatus = campaign.getCurrentStatus().getStatus().name();
        campaign.transitionTo(request.status());

        log.info("Campaign status changed: id={} {} -> {} by={}",
                campaignId, previousStatus, request.status(), authenticatedUser.getId());

        return CampaignResponse.from(campaign);
    }

    @Override
    @Transactional
    public void finalizeExpiredDonationAndFundraisingCampaigns() {
        List<? extends Campaign<?, ?>> expired = campaignRepository
                .findExpiredDonationAndFundraisingCampaigns(LocalDate.now());

        if (!expired.isEmpty()) {
            log.info("Finalizing {} expired donation/fundraising campaigns", expired.size());
            finalizeCampaigns(expired);
        }
    }

    @Override
    @Transactional
    public void finalizeExpiredNewsCampaigns() {
        List<? extends Campaign<?, ?>> expired = campaignRepository
                .findExpiredNewsCampaigns(LocalDateTime.now());

        if (!expired.isEmpty()) {
            log.info("Finalizing {} expired news campaigns", expired.size());
            finalizeCampaigns(expired);
        }
    }

    private <T extends Campaign<?, ?>> Page<CampaignResponse> toResponse(
            Page<T> campaigns
    ) {
        return campaigns.map(CampaignResponse::from);
    }

    private Campaign<?, ?> getOwnedCampaignOrThrow(UUID campaignId, User authenticatedUser) {
        Campaign<?, ?> campaign = getCampaignOrThrowException(campaignId);

        validateOwner(campaign, authenticatedUser);
        return campaign;
    }

    private Campaign<?, ?> getCampaignOrThrowException(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La campaña no existe"
                        )
                );
    }

    private void validateOwner(Campaign<?, ?> campaign, User authenticatedUser) {
        if (!campaign.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el dueño puede editar la campaña"
            );
        }
    }

    private void validateCampaignType(Campaign<?, ?> campaign, CampaignType requestType) {
        if (!campaign.getCampaignType().equals(requestType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede modificar el tipo de una campaña existente"
            );
        }
    }

    private void finalizeCampaigns(List<? extends Campaign<?, ?>> campaigns) {
        campaigns.stream()
                .filter(Campaign::isFinalizableByExpiration)
                .forEach(campaign -> {
                    campaign.transitionTo("FINISHED");
                    log.info("Campaign automatically finalized by expiration: id={}", campaign.getId());
                });
    }
}