package com.jerry.rtfd.service;

import com.jerry.rtfd.config.FraudRulesConfig;
import com.jerry.rtfd.model.Transaction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDetectionService {
    private final FraudRulesConfig fraudRulesConfig;
    private Set<String> suspiciousAccounts;

    @PostConstruct
    public void init() {
        suspiciousAccounts = ConcurrentHashMap.newKeySet();
        suspiciousAccounts.addAll(fraudRulesConfig.getSuspiciousAccounts());
        log.info("Loaded {} suspicious accounts", suspiciousAccounts.size());
        log.info("Fraud detection threshold: ${}", fraudRulesConfig.getThresholdAmount());
    }

    public void detectFraud(Transaction transaction) {
        if (isFraudulent(transaction)) {
            log.warn("🚨 FRAUD DETECTED | TXN: {} | ACCOUNT: {} | AMOUNT: ${} | MERCHANT: {}",
                    transaction.getTransactionId(),
                    transaction.getAccountId(),
                    transaction.getAmount(),
                    transaction.getMerchant());
            
            // 模拟发送告警
            logAlert(transaction);
        } else {
            log.info("✅ Valid transaction: {} | ACCOUNT: {} | AMOUNT: ${}",
                    transaction.getTransactionId(),
                    transaction.getAccountId(),
                    transaction.getAmount());
        }
    }

    private boolean isFraudulent(Transaction transaction) {
        // 规则1: 超过阈值金额
        if (transaction.getAmount() > fraudRulesConfig.getThresholdAmount()) {
            return true;
        }

        // 规则2: 可疑账户
        return suspiciousAccounts.contains(transaction.getAccountId());
    }

    private void logAlert(Transaction transaction) {
        // 模拟发送SMS
        log.warn("📱 SMS ALERT: Fraud detected for account {} - amount ${}",
                transaction.getAccountId(), transaction.getAmount());
        
        // 模拟发送Email
        log.warn("✉️ EMAIL ALERT: Fraud detected - TXN ID: {}, Account: {}, Amount: ${}",
                transaction.getTransactionId(),
                transaction.getAccountId(),
                transaction.getAmount());
    }
}