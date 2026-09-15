package com.nexo.manada_solidaria_backend.common.configs;

import nl.martijndwars.webpush.PushService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class PushTestConfiguration {

    @Bean
    public PushService pushService() {
        return Mockito.mock(PushService.class);
    }
}
