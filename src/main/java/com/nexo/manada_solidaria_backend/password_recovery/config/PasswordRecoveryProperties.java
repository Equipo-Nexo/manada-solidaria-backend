package com.nexo.manada_solidaria_backend.password_recovery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.password-recovery")
@Getter
@Setter
public class PasswordRecoveryProperties {
    private long codeExpiration;
    private long tokenExpiration;
    private int maxAttempts;
    private long resendCooldown;
}
