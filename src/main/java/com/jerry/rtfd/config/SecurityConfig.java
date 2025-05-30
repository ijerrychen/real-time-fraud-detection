package com.jerry.rtfd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class SecurityConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        
        // 加载多个配置文件，secrets.properties 优先级最高
        Resource[] resources = new Resource[] {
            new ClassPathResource("secrets.properties"),
            new ClassPathResource("application.properties")
        };
        
        configurer.setLocations(resources);
        configurer.setIgnoreResourceNotFound(true); // 忽略未找到的 secrets.properties
        configurer.setIgnoreUnresolvablePlaceholders(true);
        
        return configurer;
    }
}