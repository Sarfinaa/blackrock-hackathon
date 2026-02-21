package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.FilterRequest;
import com.blackrock.challenge.dto.FilterResponse;
import com.blackrock.challenge.model.Period;
import com.blackrock.challenge.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.blackrock.challenge.dto.ValidatorResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemporalEngineServiceTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private CeilingService ceilingService;

    @InjectMocks
    private TemporalEngineService temporalEngineService;

    @BeforeEach
    void setUp() {
        // Mock default behavior for dependencies
        when(validationService.validate(any())).thenAnswer(invocation -> {
            FilterRequest req = invocation.getArgument(0);
            ValidatorResponse resp = new ValidatorResponse();
            resp.setValid(req.getTransactions() == null ? new ArrayList<>() : req.getTransactions());
            resp.setInvalid(new ArrayList<>());
            return resp;
        });

        when(ceilingService.calculateCeilings(any())).thenAnswer(invocation -> {
            List<Transaction> list = invocation.getArgument(0);
            if (list != null) {
                for (Transaction t : list) {
                    t.setRemanent(50.0); // Base remanent simulated for tests as 50
                }
            }
            return list;
        });
    }

    private Period createPeriod(String start, String end, double value) {
        Period p = new Period();
        p.setStart(start);
        p.setEnd(end);
        p.setValue(value);
        return p;
    }

    @Test
    void qPeriod_fixedOverride_appliesLatestStartTiebreaker() {
        FilterRequest req = new FilterRequest();
        req.setMaxInvestment(50000);
        req.setWage(50000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-15 10:00:00", 150) // Normally remanent is 50
        )));

        // Two overlapping Q periods. The second starts later.
        req.setqPeriods(Arrays.asList(
                createPeriod("2024-01-01 00:00:00", "2024-01-31 23:59:59", 500),
                createPeriod("2024-01-10 00:00:00", "2024-01-20 23:59:59", 800)));

        FilterResponse res = temporalEngineService.processFilter(req);

        // K period omitted in request means it returns no groups (or empty) but we only
        // care about the remanent value.
        // Wait, if no k_periods, kPeriods map is empty. But the valid list is still
        // computed.
        // We can't access valid directly from FilterResponse, so we must add a kPeriod
        // to capture the transaction.
        req.setkPeriods(Arrays.asList(
                createPeriod("2024-01-01 00:00:00", "2024-12-31 23:59:59", 0)));
        res = temporalEngineService.processFilter(req);

        List<Transaction> validTrans = res.getValid();
        assertNotNull(validTrans);
        assertEquals(1, validTrans.size());
        assertEquals(800.0, validTrans.get(0).getRemanent()); // Overridden by Q period
    }

    @Test
    void pPeriod_bonusAddition_cumulatesAllOverlaps() {
        FilterRequest req = new FilterRequest();
        req.setMaxInvestment(50000);
        req.setWage(50000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-15 10:00:00", 250) // Normally remanent is 50
        )));

        // Three P periods, two overlap
        req.setpPeriods(Arrays.asList(
                createPeriod("2024-01-01 00:00:00", "2024-01-31 23:59:59", 10),
                createPeriod("2024-01-10 00:00:00", "2024-01-20 23:59:59", 20),
                createPeriod("2024-02-01 00:00:00", "2024-02-10 23:59:59", 30)));

        req.setkPeriods(Arrays.asList(createPeriod("2024-01-01 00:00:00", "2024-12-31 23:59:59", 0)));
        FilterResponse res = temporalEngineService.processFilter(req);

        Transaction t = res.getValid().get(0);
        assertEquals(50.0 + 10.0 + 20.0, t.getRemanent()); // 80.0
    }

    @Test
    void qAndPPeriods_combo_qAppliedBeforeP() {
        FilterRequest req = new FilterRequest();
        req.setMaxInvestment(50000);
        req.setWage(50000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-15 10:00:00", 250) // Normally remanent is 50
        )));

        req.setqPeriods(Arrays.asList(createPeriod("2024-01-01 00:00:00", "2024-01-31 23:59:59", 500)));
        req.setpPeriods(Arrays.asList(createPeriod("2024-01-01 00:00:00", "2024-01-31 23:59:59", 100)));
        req.setkPeriods(Arrays.asList(createPeriod("2024-01-01 00:00:00", "2024-12-31 23:59:59", 0)));

        FilterResponse res = temporalEngineService.processFilter(req);

        Transaction t = res.getValid().get(0);
        // Base(50) -> Q(500) -> P(+100) = 600
        assertEquals(600.0, t.getRemanent());
    }

    @Test
    void kPeriod_grouping_sameTransactionInMultipleGroups() {
        FilterRequest req = new FilterRequest();
        req.setMaxInvestment(50000);
        req.setWage(50000);
        req.setTransactions(new ArrayList<>(Arrays.asList(
                new Transaction("2024-01-15 10:00:00", 250))));

        req.setkPeriods(Arrays.asList(
                createPeriod("2024-01-01 00:00:00", "2024-01-31 23:59:59", 0),
                createPeriod("2024-01-10 00:00:00", "2024-01-20 23:59:59", 0)));

        FilterResponse res = temporalEngineService.processFilter(req);

        assertNotNull(res.getValid());
        assertEquals(1, res.getValid().size());
        assertEquals(250.0, res.getValid().get(0).getAmount());
        assertTrue(res.getValid().get(0).getInKPeriod());
    }
}
