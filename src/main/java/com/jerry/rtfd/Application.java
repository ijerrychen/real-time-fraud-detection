package com.jerry.rtfd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@PropertySource(
        value = "classpath:secrets.properties",
        ignoreResourceNotFound = true // 允许文件不存在
)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}