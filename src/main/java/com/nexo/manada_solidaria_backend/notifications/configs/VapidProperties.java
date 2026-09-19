package com.nexo.manada_solidaria_backend.notifications.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push.vapid")
public record VapidProperties(
        String publicKey,
        String privateKey,
        String subject
) {
}
