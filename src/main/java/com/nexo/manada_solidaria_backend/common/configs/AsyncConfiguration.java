package com.nexo.manada_solidaria_backend.common.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfiguration {

    @Bean("notificationExecutor")
    @Profile("!test")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("notification-");

        executor.initialize();

        return executor;
    }

    @Bean("notificationExecutor")
    @Profile("test")
    public Executor testNotificationExecutor() {
        return new SyncTaskExecutor();
    }

    @Bean(name = "mapExecutor", destroyMethod = "close")
    @Profile("!test")
    public Executor mapExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean("mapExecutor")
    @Profile("test")
    public Executor testMapExecutor() {
        return new SyncTaskExecutor();
    }
}
