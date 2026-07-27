package com.nexo.manada_solidaria_backend.images.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class R2Configuration {
    @Bean
    public S3Presigner r2Presigner(R2Properties properties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKeyId(),
                properties.getSecretAccessKey()
        );

        return S3Presigner.builder()
                .region(Region.of("auto"))
                .endpointOverride(URI.create(
                        "https://" + properties.getAccountId()
                                + ".r2.cloudflarestorage.com"
                ))
                .credentialsProvider(
                        StaticCredentialsProvider.create(credentials)
                )
                .build();
    }
}
