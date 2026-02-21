package com.blackrock.challenge.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PerformanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void performanceEndpoint_returnsValidMetrics() throws Exception {
        mockMvc.perform(get("/blackrock/challenge/v1/performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_time", matchesRegex("^\\d{2}:\\d{2}:\\d{2}\\.\\d{3}$")))
                .andExpect(jsonPath("$.memory_usage", matchesRegex("^[0-9]+\\.[0-9]{2} MB$")))
                .andExpect(jsonPath("$.current_thread_count", greaterThan(0)));
    }
}
