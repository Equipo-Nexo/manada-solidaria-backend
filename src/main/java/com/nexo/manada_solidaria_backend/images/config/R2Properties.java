package com.nexo.manada_solidaria_backend.images.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloudflare.r2")
@Getter
@Setter
public class  R2Properties  {
        String accountId;
        String accessKeyId;
        String secretAccessKey;
        String bucket;
        String publicUrl;
}
