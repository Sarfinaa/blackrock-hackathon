package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.FilterResponse;
import com.blackrock.challenge.dto.ReturnsRequest;
import com.blackrock.challenge.dto.ReturnsResponse;
import com.blackrock.challenge.model.Period;
import com.blackrock.challenge.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReturnsServiceTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private TemporalEngineService temporalEngineService;

    @InjectMocks
    private ReturnsService returnsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void setupMocksFor(double transactionSum) {
        // Mock Validation/Temporal Engine Output
        FilterResponse filterResponse = new FilterResponse();
        List<Transaction> validTrans = new ArrayList<>();
        Transaction mockTrans = new Transaction();
        mockTrans.setAmount(transactionSum);
        mockTrans.setCeiling(transactionSum);
        mockTrans.setRemanent(transactionSum);
        validTrans.add(mockTrans);
        filterResponse.setValid(validTrans);

        when(temporalEngineService.processFilter(any(ReturnsRequest.class))).thenReturn(filterResponse);

        // Mock Custom K period sum
        Map<Period, Double> sumsPerK = new HashMap<>();
        Period kMock = new Period();
        kMock.setStart("2023-01-01 00:00:00");
        kMock.setEnd("2023-12-31 23:59:00");
        sumsPerK.put(kMock, transactionSum);

        when(temporalEngineService.calculateKPeriodSums(any(), any())).thenReturn(sumsPerK);
    }

    @Test
    void calculateNps_investedLessThan10Percent_taxBenefitIsInvestedAmount() {
        setupMocksFor(50000.0);

        ReturnsRequest req = new ReturnsRequest();
        req.setAge(30); // 30 years to invest
        req.setWage(1000000); // 10% is 100,000
        req.setInflationRate(0.05); // 5% inflation

        ReturnsResponse res = returnsService.calculateNps(req);

        assertEquals(50000.0, res.getTotalCeiling());
        assertEquals(50000.0, res.getSavingsByDates().get(0).getTaxBenefit()); // Min(50k, 100k, 200k)

        // 50000 * (1 + 0.0711)^30 / (1 + 0.05)^30 = 50000 * 7.859 / 4.321 = 90918.49
        assertTrue(res.getSavingsByDates().get(0).getProfit() > 40000.0);
    }

    @Test
    void calculateNps_maxesOutAt2Lakhs() {
        setupMocksFor(300000.0);

        ReturnsRequest req = new ReturnsRequest();
        req.setAge(58); // 2 years to 60 -> min investment period is 5 years
        req.setWage(5000000); // 10% is 500,000
        req.setInflationRate(0.05);

        ReturnsResponse res = returnsService.calculateNps(req);

        assertEquals(300000.0, res.getTotalCeiling());
        assertEquals(200000.0, res.getSavingsByDates().get(0).getTaxBenefit()); // Min(300k, 500k, 200k cap)
    }

    @Test
    void calculateIndex_zeroTaxBenefit() {
        setupMocksFor(50000.0);

        ReturnsRequest req = new ReturnsRequest();
        req.setAge(30);
        req.setWage(1000000);
        req.setInflationRate(0.05);

        ReturnsResponse res = returnsService.calculateIndex(req);

        assertEquals(50000.0, res.getTotalCeiling());
        assertEquals(0.0, res.getSavingsByDates().get(0).getTaxBenefit()); // Index has zero tax benefit

        // 14.49% return should be much higher than NPS
        assertTrue(res.getSavingsByDates().get(0).getProfit() > 100000.0);
    }
}
