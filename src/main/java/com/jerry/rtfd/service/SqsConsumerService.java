package com.jerry.rtfd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerry.rtfd.model.Transaction;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
public class SqsConsumerService implements ErrorHandler {
    private final FraudDetectionService fraudDetectionService;
    private final ObjectMapper objectMapper;
    private final Executor asyncTaskExecutor;

    @SqsListener("${app.sqs.queue-name}")
    public void receiveMessage(String message) {
        log.debug("Received SQS message: {}", message);
        asyncTaskExecutor.execute(() -> {
            try {
                Transaction transaction = objectMapper.readValue(message, Transaction.class);
                fraudDetectionService.detectFraud(transaction);
            } catch (Exception e) {
                log.error("❌ Error processing SQS message: {}", e.getMessage());
                log.debug("Failed message content: {}", message);
            }
        });
    }

    @Override
    public void handleError(Throwable t) {
        log.error("❌ SQS Listener Error: {}", t.getMessage(), t);
    }
}