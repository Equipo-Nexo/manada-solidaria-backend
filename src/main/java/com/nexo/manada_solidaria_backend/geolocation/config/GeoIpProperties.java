package com.nexo.manada_solidaria_backend.geolocation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "geoip")
@Getter
@Setter
public class GeoIpProperties {
    private String baseUrl;
    private Duration connectTimeout;
    private Duration readTimeout;
}
