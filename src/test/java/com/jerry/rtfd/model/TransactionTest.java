package com.jerry.rtfd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    @Test
    public void testTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("123");
        transaction.setAccountId("test");
        transaction.setAmount(100.0);
        transaction.setMerchant("testMerchant");
        transaction.setTimestamp(123456789);

        assertEquals("123", transaction.getTransactionId());
        assertEquals("test", transaction.getAccountId());
        assertEquals(100.0, transaction.getAmount());
        assertEquals("testMerchant", transaction.getMerchant());
        assertEquals(123456789, transaction.getTimestamp());
    }
}