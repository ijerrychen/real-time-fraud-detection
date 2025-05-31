package com.jerry.rtfd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    // 获取容器分配的CPU核心数
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 设置核心线程数
        executor.setCorePoolSize(CPU_CORES * 2);
        
        // 设置最大线程数
        executor.setMaxPoolSize(CPU_CORES * 20);
        
        // 设置队列容量
        executor.setQueueCapacity(CPU_CORES * 100);
        
        // 设置线程存活时间
        executor.setKeepAliveSeconds(60);
        
        // 设置线程名称前缀
        executor.setThreadNamePrefix("fraud-detection-");
        
        // 设置拒绝策略（CallerRunsPolicy）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 不允许核心线程超时（保持核心线程常驻）
        executor.setAllowCoreThreadTimeOut(false);
        
        // 初始化执行器
        executor.initialize();
        
        return executor;
    }
}