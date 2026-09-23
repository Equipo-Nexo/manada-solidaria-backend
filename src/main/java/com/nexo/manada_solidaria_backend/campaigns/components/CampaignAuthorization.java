package com.nexo.manada_solidaria_backend.campaigns.components;

import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CampaignAuthorization {

    private final CampaignRepository campaignRepository;

    public boolean isOwner(UUID campaignId, User user) {
        return campaignRepository.findById(campaignId)
                .map(campaign -> campaign.getOwner().getId().equals(user.getId()))
                .orElse(true);
    }
}
