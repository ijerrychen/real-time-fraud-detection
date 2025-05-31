package com.jerry.rtfd.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ThreadPoolConfigTest {

    @Test
    public void testAsyncTaskExecutor() {
        ThreadPoolConfig config = new ThreadPoolConfig();
        ThreadPoolTaskExecutor executor = config.asyncTaskExecutor();
        assertNotNull(executor);
    }
}