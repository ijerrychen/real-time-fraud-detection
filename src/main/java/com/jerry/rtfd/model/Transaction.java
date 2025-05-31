package com.jerry.rtfd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
public class Transaction {
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    @NotBlank(message = "Account ID is required")
    private String accountId;
    
    @Positive(message = "Amount must be positive")
    private double amount;
    
    @NotBlank(message = "Merchant is required")
    private String merchant;
    
    @Positive(message = "Timestamp must be positive")
    private long timestamp;
}