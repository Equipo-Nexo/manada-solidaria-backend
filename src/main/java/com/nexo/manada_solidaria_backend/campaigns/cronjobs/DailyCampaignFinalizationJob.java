package com.nexo.manada_solidaria_backend.campaigns.cronjobs;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyCampaignFinalizationJob {

    private final CampaignService campaignService;

    @Scheduled(cron = "0 0 0 * * *")
    public void finalizeExpiredDonationAndFundraisingCampaigns() {
        log.info("Starting scheduled job: finalizeExpiredDonationAndFundraisingCampaigns");
        campaignService.finalizeExpiredDonationAndFundraisingCampaigns();
        log.info("Completed scheduled job: finalizeExpiredDonationAndFundraisingCampaigns");
    }
}