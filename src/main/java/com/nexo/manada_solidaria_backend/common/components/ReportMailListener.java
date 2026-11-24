package com.nexo.manada_solidaria_backend.common.components;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.common.configs.ReportProperties;
import com.nexo.manada_solidaria_backend.common.data.enums.ReportableType;
import com.nexo.manada_solidaria_backend.common.mail.ReportMailer;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class ReportMailListener {

    private final ReportMailer reportMailer;
    private final ReportProperties reportProperties;
    private final CampaignService campaignService;

    @EventListener
    public void onReportThresholdReached(ReportThresholdReachedEvent event) {
        String postUrl = buildPostUrl(event.postId(), event.type());
        reportMailer.send(reportProperties.adminEmail(), new ReportMailer.ReportInformation(postUrl));
    }

    private String buildPostUrl(UUID postId, ReportableType type) {
        String baseUrl = reportProperties.frontendUrl().replaceAll("/+$", "");
        String path = switch (type) {
            case ANIMAL_POST -> "/animal/detalle/" + postId;
            case CAMPAIGN -> resolveCampaignPath(postId);
        };
        return baseUrl + path;
    }

    private String resolveCampaignPath(UUID campaignId) {
        CampaignType campaignType = campaignService.getCampaignType(campaignId);
        return campaignType == CampaignType.FUNDRAISING
                ? "/colectas/" + campaignId
                : "/campanias/" + campaignId;
    }

    public record ReportThresholdReachedEvent(
            UUID postId,
            ReportableType type
    ) {
    }
}