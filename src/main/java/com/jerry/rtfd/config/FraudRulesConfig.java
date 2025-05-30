package com.jerry.rtfd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@PropertySource("classpath:fraud-rules.properties")
@ConfigurationProperties(prefix = "rule")
@Data
public class FraudRulesConfig {
    private double thresholdAmount;
    private List<String> suspiciousAccounts;
}