package com.blackrock.challenge.controller;

import com.blackrock.challenge.dto.ReturnsRequest;
import com.blackrock.challenge.model.Period;
import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReturnsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testNpsReturnsEndpoint_FullLogicPipeline() throws Exception {
        ReturnsRequest req = new ReturnsRequest();
        req.setAge(29);
        req.setWage(1000000); // 10% is 100k
        req.setInflationRate(0.055);
        req.setWage(50000);

        // 2023-07-01 (620) overriding ceiling logic inside engine
        Transaction t1 = new Transaction();
        t1.setDate("2023-07-01 21:59:00");
        t1.setAmount(620);

        req.setTransactions(Arrays.asList(t1));

        Period k1 = new Period();
        k1.setStart("2023-01-01 00:00:00");
        k1.setEnd("2023-12-31 23:59:00");

        req.setkPeriods(Arrays.asList(k1));

        mockMvc.perform(post("/blackrock/challenge/v1/returns:nps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savingsByDates", hasSize(1)))
                // Investment Period: 60 - 29 = 31 years
                // 620 goes to 700 ceiling -> remanent 80
                // 80 invested
                .andExpect(jsonPath("$.totalTransactionAmount", is(620.0)))
                .andExpect(jsonPath("$.totalCeiling", is(700.0)))
                .andExpect(jsonPath("$.savingsByDates[0].amount", is(80.0)))
                .andExpect(jsonPath("$.savingsByDates[0].taxBenefit", is(80.0))); // Max rule applies
    }
}
