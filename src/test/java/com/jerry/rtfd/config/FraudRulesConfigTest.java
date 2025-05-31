package com.jerry.rtfd.config;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FraudRulesConfigTest {

    @Test
    public void testFraudRulesConfig() {
        FraudRulesConfig config = new FraudRulesConfig();
        config.setThresholdAmount(10000);
        List<String> accounts = Arrays.asList("pandas", "monkey", "tiger");
        config.setSuspiciousAccounts(accounts);

        assertEquals(10000, config.getThresholdAmount());
        assertEquals(accounts, config.getSuspiciousAccounts());
    }
}