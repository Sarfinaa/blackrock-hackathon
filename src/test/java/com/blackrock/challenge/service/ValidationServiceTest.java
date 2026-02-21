package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.ValidatorRequest;
import com.blackrock.challenge.dto.ValidatorResponse;
import com.blackrock.challenge.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    @Test
    void allValid_returnsOnlyValidArray() {
        ValidatorRequest req = new ValidatorRequest();
        req.setMaxInvestment(1000);
        req.setWage(2000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-01", 100),
                new Transaction("2024-01-02", 200))));

        ValidatorResponse res = validationService.validate(req);
        assertEquals(2, res.getValid().size());
        assertEquals(0, res.getInvalid().size());
    }

    @Test
    void negatives_rejectedWithCorrectMessage() {
        ValidatorRequest req = new ValidatorRequest();
        req.setMaxInvestment(1000);
        req.setWage(2000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-01", 100),
                new Transaction("2024-01-02", -50))));

        ValidatorResponse res = validationService.validate(req);
        assertEquals(1, res.getValid().size());
        assertEquals(1, res.getInvalid().size());
        assertEquals("Amount cannot be negative", res.getInvalid().get(0).getMessage());
    }

    @Test
    void duplicates_rejectedWithCorrectMessage() {
        ValidatorRequest req = new ValidatorRequest();
        req.setMaxInvestment(1000);
        req.setWage(2000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-01", 100),
                new Transaction("2024-01-01", 100) // duplicate date + amount
        )));

        ValidatorResponse res = validationService.validate(req);
        assertEquals(1, res.getValid().size());
        assertEquals(1, res.getInvalid().size());
        assertEquals("Duplicate transaction detected", res.getInvalid().get(0).getMessage());
    }

    @Test
    void differentAmountSameDate_notDuplicate() {
        ValidatorRequest req = new ValidatorRequest();
        req.setMaxInvestment(1000);
        req.setWage(2000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-01", 100),
                new Transaction("2024-01-01", 150) // same date, different amount
        )));

        ValidatorResponse res = validationService.validate(req);
        assertEquals(2, res.getValid().size());
        assertEquals(0, res.getInvalid().size());
    }

}
