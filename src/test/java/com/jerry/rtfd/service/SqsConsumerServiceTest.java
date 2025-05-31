package com.jerry.rtfd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerry.rtfd.model.Transaction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

public class SqsConsumerServiceTest {

    @Test
    public void testReceiveMessage() throws Exception {
        FraudDetectionService fraudDetectionService = mock(FraudDetectionService.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        Executor asyncTaskExecutor = mock(ThreadPoolTaskExecutor.class);
        SqsConsumerService service = new SqsConsumerService(fraudDetectionService, objectMapper, asyncTaskExecutor);

        String message = "{\"transactionId\":\"123\",\"accountId\":\"test\",\"amount\":100.0,\"merchant\":\"testMerchant\",\"timestamp\":123456789}";
        Transaction transaction = new Transaction();
        transaction.setTransactionId("123");

        when(objectMapper.readValue(message, Transaction.class)).thenReturn(transaction);

        service.receiveMessage(message);

        verify(asyncTaskExecutor, times(1)).execute(Mockito.any(Runnable.class));
    }
}