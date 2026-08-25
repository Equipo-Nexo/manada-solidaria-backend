package com.nexo.manada_solidaria_backend.campaigns.cronjobs;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyCampaignFinalizationJob {

    private final CampaignService campaignService;

    @Scheduled(cron = "0 0 0 * * *")
    public void finalizeExpiredDonationAndFundraisingCampaigns() {
        campaignService.finalizeExpiredDonationAndFundraisingCampaigns();
    }
}