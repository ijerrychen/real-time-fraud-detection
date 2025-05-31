package com.jerry.rtfd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerry.rtfd.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class SqsProducerService {
    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;
    @Value("${app.sqs.queue-name}")
    private String queueName;

    public void sendTransaction(Transaction transaction) {
        try {
            String messageBody = objectMapper.writeValueAsString(transaction);
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(getQueueUrl())
                    .messageBody(messageBody)
                    .build();

            sqsAsyncClient.sendMessage(request).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message to SQS", ex);
                } else {
                    log.info("Transaction sent to SQS: {}", transaction.getTransactionId());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing transaction", e);
        }
    }

    private String getQueueUrl() {
        return queueUrl + queueName;
    }
}