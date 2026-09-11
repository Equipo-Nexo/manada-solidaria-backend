package com.nexo.manada_solidaria_backend.password_recovery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.password-recovery")
public record PasswordRecoveryProperties(
        long codeExpiration,
        long tokenExpiration,
        int maxAttempts,
        long resendCooldown
) {
}
