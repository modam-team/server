package com.example.modam.global.config.ExcutorConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AladinAsyncConfig {

    @Bean(name = "aladin")
    public ThreadPoolTaskExecutor myExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(30);
        executor.setMaxPoolSize(60);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("aladin_search-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        executor.initialize();

        return executor;
    }
}
