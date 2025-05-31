package com.jerry.rtfd.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AwsConfigTest {

    @Test
    public void testSqsAsyncClient() {
        Environment env = mock(Environment.class);
        when(env.getProperty("AWS_REGION", env.getProperty("aws.region", "us-east-2"))).thenReturn("us-east-2");
        AwsConfig config = new AwsConfig(env);
        SqsAsyncClient client = config.sqsAsyncClient();
        assertNotNull(client);
    }
}