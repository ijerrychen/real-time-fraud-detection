package com.jerry.rtfd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    // 获取容器分配的CPU核心数
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();
    @Bean
    public Executor asyncTaskExecutor() {
        return new ThreadPoolExecutor(
                CPU_CORES * 2,
                CPU_CORES * 20,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(CPU_CORES * 100),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}