package com.jerry.rtfd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@Slf4j
public class AwsConfig {
    private final Environment env;
    
    public AwsConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .region(Region.of(getAwsRegion()))
                .credentialsProvider(getAwsCredentialsProvider())
                .build();
    }

    
    private String getAwsRegion() {
        // 优先级：环境变量 > 配置文件 > 默认值
        return env.getProperty("AWS_REGION", 
               env.getProperty("aws.region", "us-east-2"));
    }
    
    private AwsCredentialsProvider getAwsCredentialsProvider() {
        // 使用默认凭证链（环境变量、系统属性、~/.aws/credentials等）
        AwsCredentialsProvider provider = DefaultCredentialsProvider.builder()
                .asyncCredentialUpdateEnabled(true) // 启用异步凭证更新
                .build();

        // 添加调试日志
        try {
            System.out.println("Resolved credentials: " + provider.resolveCredentials().accessKeyId());
            System.out.println("Region: " + getAwsRegion());
        } catch (Exception e) {
            System.err.println("Error resolving credentials: " + e.getMessage());
        }

        return provider;
    }
}