package com.nexo.manada_solidaria_backend.campaigns.cronjobs;

import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsCampaignFinalizationJob {

    private final CampaignService campaignService;

    @Scheduled(cron = "0 */15 * * * *")
    public void finalizeExpiredNewsCampaigns() {
        log.debug("Running scheduled job: finalizeExpiredNewsCampaigns");
        campaignService.finalizeExpiredNewsCampaigns();
    }
}