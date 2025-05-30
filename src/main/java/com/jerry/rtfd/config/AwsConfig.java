package com.jerry.rtfd.config;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
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
        // 1. 检查环境变量
        String accessKey = env.getProperty("AWS_ACCESS_KEY_ID");
        String secretKey = env.getProperty("AWS_SECRET_ACCESS_KEY");
        
        if (accessKey != null && !accessKey.isEmpty() && 
            secretKey != null && !secretKey.isEmpty()) {
            return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            );
        }
        
        // 2. 检查文件路径（用于Docker Secrets）
        String accessKeyFile = env.getProperty("AWS_ACCESS_KEY_ID_FILE");
        String secretKeyFile = env.getProperty("AWS_SECRET_ACCESS_KEY_FILE");
        
        if (accessKeyFile != null && secretKeyFile != null) {
            try {
                String accessKeyContent = new String(Files.readAllBytes(Paths.get(accessKeyFile))).trim();
                String secretKeyContent = new String(Files.readAllBytes(Paths.get(secretKeyFile))).trim();
                return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyContent, secretKeyContent)
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to read AWS credentials from files", e);
            }
        }
        
        // 3. 使用默认凭证链（环境变量、系统属性、~/.aws/credentials等）
        return DefaultCredentialsProvider.create();
    }
}