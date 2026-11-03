package com.nexo.manada_solidaria_backend.common.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.report")
public record ReportProperties(
        long threshold,
        String adminEmail,
        String frontendUrl
) {
}