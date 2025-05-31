package com.jerry.rtfd;

import com.jerry.rtfd.controller.TransactionController;
import com.jerry.rtfd.service.SqsProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SqsProducerService sqsProducerService;

    @Test
    void processTransaction_ShouldReturnAccepted() throws Exception {
        String transactionJson = "{\"transactionId\":\"TXN123\",\"accountId\":\"ACCT100\",\"amount\":5000.0,\"merchant\":\"OnlineStore\",\"timestamp\":1678901234}";

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isAccepted());
    }
}