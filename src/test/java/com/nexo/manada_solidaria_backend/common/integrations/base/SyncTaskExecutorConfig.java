package com.nexo.manada_solidaria_backend.common.integrations.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class SyncTaskExecutorConfig {

    @Bean
    TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}
