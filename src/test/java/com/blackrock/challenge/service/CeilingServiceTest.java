package com.blackrock.challenge.service;

import com.blackrock.challenge.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CeilingServiceTest {

    private CeilingService ceilingService;

    @BeforeEach
    void setUp() {
        ceilingService = new CeilingService();
    }

    @Test
    void exactMultipleOf100_ceilingUnchanged() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("2024-01-01 10:00:00", 100));

        List<Transaction> result = ceilingService.calculateCeilings(transactions);

        assertEquals(100.0, result.get(0).getCeiling());
        assertEquals(0.0, result.get(0).getRemanent());
    }

    @Test
    void justAboveMultiple_roundsToNext100() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("2024-01-01 10:00:00", 101));

        List<Transaction> result = ceilingService.calculateCeilings(transactions);

        assertEquals(200.0, result.get(0).getCeiling());
        assertEquals(99.0, result.get(0).getRemanent());
    }

    @Test
    void zeroAmount_returnsZeroCeilingAndRemanent() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("2024-01-01 10:00:00", 0));

        List<Transaction> result = ceilingService.calculateCeilings(transactions);

        assertEquals(0.0, result.get(0).getCeiling());
        assertEquals(0.0, result.get(0).getRemanent());
    }

    @Test
    void multipleTransactions_allEnriched() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("2024-01-01 10:00:00", 250));
        transactions.add(new Transaction("2024-01-02 10:00:00", 500));
        transactions.add(new Transaction("2024-01-03 10:00:00", 1));

        List<Transaction> result = ceilingService.calculateCeilings(transactions);

        assertEquals(3, result.size());
        assertEquals(300.0, result.get(0).getCeiling());
        assertEquals(50.0, result.get(0).getRemanent());
        assertEquals(500.0, result.get(1).getCeiling());
        assertEquals(0.0, result.get(1).getRemanent());
        assertEquals(100.0, result.get(2).getCeiling());
        assertEquals(99.0, result.get(2).getRemanent());
    }

    @Test
    void largeAmount_handledCorrectly() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("2024-01-01 10:00:00", 99999));

        List<Transaction> result = ceilingService.calculateCeilings(transactions);

        assertEquals(100000.0, result.get(0).getCeiling());
        assertEquals(1.0, result.get(0).getRemanent());
    }
}
