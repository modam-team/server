package com.example.modam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "aladin")
    public Executor myExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 실험용 기본값 (필요에 따라 조절)
        executor.setCorePoolSize(20);          // 기본 스레드 수
        executor.setMaxPoolSize(100);          // 최대 스레드 수
        executor.setQueueCapacity(500);        // 대기 큐 크기
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("my-exec-");

        // 큐가 찼을 때 호출한 스레드가 직접 실행
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
