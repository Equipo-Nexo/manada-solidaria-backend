package com.nexo.manada_solidaria_backend.notifications.configs;

import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;
import java.security.Security;

@Configuration
@RequiredArgsConstructor
public class WebPushConfiguration {

    private final VapidProperties properties;

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