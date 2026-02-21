package com.blackrock.challenge.controller;

import com.blackrock.challenge.dto.ValidatorRequest;
import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ValidatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void validatorEndpoint_processesTransactionsCorrectly() throws Exception {
        ValidatorRequest request = new ValidatorRequest();
        request.setMaxInvestment(500);
        request.setWage(300);
        request.setTransactions(Arrays.asList(
                new Transaction("2024-01-01 10:00:00", 100), // Valid
                new Transaction("2024-01-02 10:00:00", -50), // Invalid: Negative
                new Transaction("2024-01-01 10:00:00", 100), // Invalid: Duplicate
                new Transaction("2024-01-03 10:00:00", 150), // Valid (Sum = 250)
                new Transaction("2024-01-04 10:00:00", 100) // Valid (Bounds checks removed)
        ));

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:validator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid", hasSize(3)))
                .andExpect(jsonPath("$.invalid", hasSize(2)))
                .andExpect(jsonPath("$.valid[0].amount").value(100.0))
                .andExpect(jsonPath("$.valid[1].amount").value(150.0))
                .andExpect(jsonPath("$.valid[2].amount").value(100.0))
                .andExpect(jsonPath("$.invalid[0].message").value("Amount cannot be negative"))
                .andExpect(jsonPath("$.invalid[1].message").value("Duplicate transaction detected"));
    }
}
