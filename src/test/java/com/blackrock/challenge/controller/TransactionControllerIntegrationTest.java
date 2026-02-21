package com.blackrock.challenge.controller;

import com.blackrock.challenge.dto.CeilingRequest;
import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void ceilingEndpoint_calculatesCorrectly() throws Exception {
        List<Transaction> request = Arrays.asList(
                new Transaction("2024-01-01 10:00:00", 100),
                new Transaction("2024-01-02 10:00:00", 101),
                new Transaction("2024-01-03 10:00:00", 250));

        mockMvc.perform(post("/blackrock/challenge/v1/transactions:parse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].ceiling").value(100.0))
                .andExpect(jsonPath("$[0].remanent").value(0.0))
                .andExpect(jsonPath("$[1].ceiling").value(200.0))
                .andExpect(jsonPath("$[1].remanent").value(99.0))
                .andExpect(jsonPath("$[2].ceiling").value(300.0))
                .andExpect(jsonPath("$[2].remanent").value(50.0));
    }
}
