package com.nexo.manada_solidaria_backend.common.components;

import com.nexo.manada_solidaria_backend.common.configs.ReportProperties;
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

    @EventListener
    public void onReportThresholdReached(ReportThresholdReachedEvent event) {
        String postUrl = reportProperties.frontendUrl() + "/posts/" + event.postId();
        reportMailer.send(reportProperties.adminEmail(), new ReportMailer.ReportInformation(postUrl));
    }

    public record ReportThresholdReachedEvent(
            UUID postId
    ) {
    }
}