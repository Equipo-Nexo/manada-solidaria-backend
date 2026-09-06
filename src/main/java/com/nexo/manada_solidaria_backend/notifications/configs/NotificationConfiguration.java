package com.nexo.manada_solidaria_backend.notifications.configs;

import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class NotificationConfiguration {

    private final VapidProperties properties;

    @Bean("notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("notification-");

        executor.initialize();

        return executor;
    }

    @Bean
    public PushService pushService() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());

        return new PushService(
                properties.publicKey(),
                properties.privateKey(),
                properties.subject()
        );
    }
}