package com.nexo.manada_solidaria_backend.common.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new
                HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectionRequestTimeout(timeout*1000);
        clientHttpRequestFactory.setReadTimeout(timeout*3000);
        return clientHttpRequestFactory;
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate(getClientHttpRequestFactory());
    }
}
