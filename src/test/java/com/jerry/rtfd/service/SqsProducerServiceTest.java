package com.jerry.rtfd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerry.rtfd.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class SqsProducerServiceTest {

    private SqsProducerService sqsProducerService;

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${aws.sqs.queueUrl.prefix}")
    private String queueUrlPrefix = "testQueueUrlPrefix";

    @Value("${app.sqs.queue-name}")
    private String queueName = "testQueueName";

    private static final Logger log = LoggerFactory.getLogger(SqsProducerServiceTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sqsProducerService = new SqsProducerService(sqsAsyncClient, objectMapper);
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field queueUrlPrefixField = SqsProducerService.class.getDeclaredField("queueUrlPrefix");
            queueUrlPrefixField.setAccessible(true);
            queueUrlPrefixField.set(sqsProducerService, queueUrlPrefix);

            java.lang.reflect.Field queueNameField = SqsProducerService.class.getDeclaredField("queueName");
            queueNameField.setAccessible(true);
            queueNameField.set(sqsProducerService, queueName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error setting private fields", e);
        }
    }

    @Test
    void testSendTransactionSuccess() throws JsonProcessingException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("123");
        transaction.setAccountId("456");
        transaction.setAmount(100.0);
        transaction.setMerchant("Test Merchant");
        transaction.setTimestamp(System.currentTimeMillis());

        String messageBody = "{\"transactionId\":\"123\",\"accountId\":\"456\",\"amount\":100.0,\"merchant\":\"Test Merchant\",\"timestamp\":1672531200000}";
        when(objectMapper.writeValueAsString(transaction)).thenReturn(messageBody);

        SendMessageResponse sendMessageResponse = SendMessageResponse.builder().build();
        CompletableFuture<SendMessageResponse> future = CompletableFuture.completedFuture(sendMessageResponse);
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        sqsProducerService.sendTransaction(transaction);

        verify(objectMapper, times(1)).writeValueAsString(transaction);
        verify(sqsAsyncClient, times(1)).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void testSendTransactionJsonProcessingException() throws JsonProcessingException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("123");
        transaction.setAccountId("456");
        transaction.setAmount(100.0);
        transaction.setMerchant("Test Merchant");
        transaction.setTimestamp(System.currentTimeMillis());

        when(objectMapper.writeValueAsString(transaction)).thenThrow(JsonProcessingException.class);

        sqsProducerService.sendTransaction(transaction);

        verify(objectMapper, times(1)).writeValueAsString(transaction);
        verify(sqsAsyncClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void testSendTransactionSqsError() throws JsonProcessingException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("123");
        transaction.setAccountId("456");
        transaction.setAmount(100.0);
        transaction.setMerchant("Test Merchant");
        transaction.setTimestamp(System.currentTimeMillis());

        String messageBody = "{\"transactionId\":\"123\",\"accountId\":\"456\",\"amount\":100.0,\"merchant\":\"Test Merchant\",\"timestamp\":1672531200000}";
        when(objectMapper.writeValueAsString(transaction)).thenReturn(messageBody);

        CompletableFuture<SendMessageResponse> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("SQS error"));
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        sqsProducerService.sendTransaction(transaction);

        verify(objectMapper, times(1)).writeValueAsString(transaction);
        verify(sqsAsyncClient, times(1)).sendMessage(any(SendMessageRequest.class));
    }
}