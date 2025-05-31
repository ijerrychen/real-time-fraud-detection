package com.jerry.rtfd.service;

import com.jerry.rtfd.config.FraudRulesConfig;
import com.jerry.rtfd.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private FraudRulesConfig fraudRulesConfig;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    private final TestLogAppender logAppender = new TestLogAppender();

    @BeforeEach
    void setUp() {
        // 初始化日志捕获器
        logAppender.start();
        
        // 修复：使用List作为返回类型
        List<String> suspiciousAccounts = Arrays.asList("suspicious-123");
        when(fraudRulesConfig.getSuspiciousAccounts()).thenReturn(suspiciousAccounts);
        
        when(fraudRulesConfig.getThresholdAmount()).thenReturn(1000.0);
        
        // 手动触发@PostConstruct方法
        fraudDetectionService.init();
    }

    @AfterEach
    void tearDown() {
        // 停止日志捕获器
        logAppender.stop();
    }

    // 测试辅助类：捕获日志输出
    private static class TestLogAppender extends ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent> {
        private final ConcurrentHashMap<String, Boolean> logMessages = new ConcurrentHashMap<>();

        @Override
        protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) {
            logMessages.put(event.getFormattedMessage(), true);
        }

        @Override
        public void start() {
            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FraudDetectionService.class);
            logger.addAppender(this);
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FraudDetectionService.class);
            logger.detachAppender(this);
        }

        boolean containsMessage(String message) {
            return logMessages.containsKey(message);
        }
        
        void clear() {
            logMessages.clear();
        }
    }

    // 创建带timestamp的Transaction对象
    private Transaction createTransaction(String id, String account, double amount, String merchant) {
        Transaction txn = new Transaction();
        txn.setTransactionId(id);
        txn.setAccountId(account);
        txn.setAmount(amount);
        txn.setMerchant(merchant);
        txn.setTimestamp(1672531200000L); // 固定时间戳：2023-01-01 00:00:00
        return txn;
    }

    @Test
    void testValidTransaction() {
        // 1. 准备测试数据（包含timestamp）
        Transaction transaction = createTransaction("txn-001", "account-001", 500.0, "Merchant A");

        // 2. 执行测试
        fraudDetectionService.detectFraud(transaction);

        // 3. 验证日志输出
        String expectedLog = "✅ Valid transaction: txn-001 | ACCOUNT: account-001 | AMOUNT: $500.0";
        assertTrue(logAppender.containsMessage(expectedLog), "未找到正常交易日志");
    }

    @Test
    void testAmountExceedsThreshold() {
        // 1. 准备测试数据（包含timestamp）
        Transaction transaction = createTransaction("txn-002", "account-002", 1500.0, "Merchant B");

        // 2. 执行测试
        fraudDetectionService.detectFraud(transaction);

        // 3. 验证欺诈日志
        String fraudLog = "🚨 FRAUD DETECTED | TXN: txn-002 | ACCOUNT: account-002 | AMOUNT: $1500.0 | MERCHANT: Merchant B";
        assertTrue(logAppender.containsMessage(fraudLog), "未找到金额超限欺诈日志");

        // 4. 验证告警日志
        String smsAlert = "📱 SMS ALERT: Fraud detected for account account-002 - amount $1500.0";
        String emailAlert = "✉️ EMAIL ALERT: Fraud detected - TXN ID: txn-002, Account: account-002, Amount: $1500.0";
        assertTrue(logAppender.containsMessage(smsAlert), "未找到SMS告警日志");
        assertTrue(logAppender.containsMessage(emailAlert), "未找到Email告警日志");
    }

    @Test
    void testSuspiciousAccount() {
        // 1. 准备测试数据（包含timestamp）
        Transaction transaction = createTransaction("txn-003", "suspicious-123", 200.0, "Merchant C");

        // 2. 执行测试
        fraudDetectionService.detectFraud(transaction);

        // 3. 验证欺诈日志
        String fraudLog = "🚨 FRAUD DETECTED | TXN: txn-003 | ACCOUNT: suspicious-123 | AMOUNT: $200.0 | MERCHANT: Merchant C";
        assertTrue(logAppender.containsMessage(fraudLog), "未找到可疑账户欺诈日志");

        // 4. 验证告警日志
        String smsAlert = "📱 SMS ALERT: Fraud detected for account suspicious-123 - amount $200.0";
        assertTrue(logAppender.containsMessage(smsAlert), "未找到SMS告警日志");
    }

    @Test
    void testInitMethod() {
        // 清除之前的日志
        logAppender.clear();
        
        // 1. 准备测试数据 - 使用List
        List<String> accounts = Arrays.asList("acc-001", "acc-002");
        when(fraudRulesConfig.getSuspiciousAccounts()).thenReturn(accounts);
        when(fraudRulesConfig.getThresholdAmount()).thenReturn(5000.0);

        // 2. 重新初始化服务
        fraudDetectionService.init();

        // 3. 验证初始化日志
        String initLog1 = "Loaded 2 suspicious accounts";
        String initLog2 = "Fraud detection threshold: $5000.0";
        assertTrue(logAppender.containsMessage(initLog1), "未找到可疑账户数量日志");
        assertTrue(logAppender.containsMessage(initLog2), "未找到阈值金额日志");

        // 4. 验证字段初始化
        Set<String> loadedAccounts = (Set<String>) ReflectionTestUtils.getField(fraudDetectionService, "suspiciousAccounts");
        assertEquals(2, loadedAccounts.size(), "可疑账户数量不正确");
        assertTrue(loadedAccounts.contains("acc-001"), "缺少预期可疑账户");
        assertTrue(loadedAccounts.contains("acc-002"), "缺少预期可疑账户");
    }

    @Test
    void testBoundaryAmount() {
        // 1. 准备测试数据 - 金额等于阈值（包含timestamp）
        Transaction transaction = createTransaction("txn-004", "account-003", 1000.0, "Merchant D");

        // 2. 执行测试
        fraudDetectionService.detectFraud(transaction);

        // 3. 验证日志 - 应视为合法交易
        String expectedLog = "✅ Valid transaction: txn-004 | ACCOUNT: account-003 | AMOUNT: $1000.0";
        assertTrue(logAppender.containsMessage(expectedLog), "边界金额交易应视为合法");
    }
}