package com.jerry.rtfd.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SecurityConfigTest {

    @Test
    public void testPropertySourcesPlaceholderConfigurer() {
        SecurityConfig config = new SecurityConfig();
        PropertySourcesPlaceholderConfigurer configurer = config.propertySourcesPlaceholderConfigurer();
        assertNotNull(configurer);
    }
}