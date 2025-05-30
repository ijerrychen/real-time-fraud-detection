package com.jerry.rtfd.controller;

import com.jerry.rtfd.model.Transaction;
import com.jerry.rtfd.service.SqsProducerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final SqsProducerService sqsProducerService;

    public TransactionController(SqsProducerService sqsProducerService) {
        this.sqsProducerService = sqsProducerService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Map<String, String>> processTransaction(@Valid @RequestBody Transaction transaction) {
        sqsProducerService.sendTransaction(transaction);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "accepted");
        response.put("transactionId", transaction.getTransactionId());
        response.put("message", "Transaction is being processed for fraud detection");
        
        return ResponseEntity.accepted().body(response);
    }
}