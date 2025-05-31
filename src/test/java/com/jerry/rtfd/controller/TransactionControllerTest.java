package com.jerry.rtfd.controller;

import com.jerry.rtfd.model.Transaction;
import com.jerry.rtfd.service.SqsProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private SqsProducerService sqsProducerService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void processTransaction_ShouldSendToSQSAndReturnAccepted() {
        // 1. 准备测试数据
        Transaction transaction = new Transaction();
        transaction.setTransactionId("txn-12345");
        
        // 2. 调用被测试方法
        ResponseEntity<Map<String, String>> response = 
            transactionController.processTransaction(transaction);
        
        // 3. 验证行为
        // 检查是否调用了SQS服务
        verify(sqsProducerService, times(1)).sendTransaction(transaction);
        verifyNoMoreInteractions(sqsProducerService);
        
        // 4. 验证响应状态码
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        // 5. 验证响应体内容
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("accepted", responseBody.get("status"));
        assertEquals("txn-12345", responseBody.get("transactionId"));
        assertEquals(
            "Transaction is being processed for fraud detection",
            responseBody.get("message")
        );
    }

    @Test
    void processTransaction_ShouldUseCorrectTransactionId() {
        // 准备特殊ID的测试数据
        Transaction transaction = new Transaction();
        String expectedId = "SPECIAL_ID_987";
        transaction.setTransactionId(expectedId);
        
        // 调用方法
        ResponseEntity<Map<String, String>> response = 
            transactionController.processTransaction(transaction);
        
        // 验证ID传递正确性
        verify(sqsProducerService).sendTransaction(transaction);
        assertEquals(
            expectedId, 
            response.getBody().get("transactionId")
        );
    }
}