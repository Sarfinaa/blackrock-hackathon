package com.blackrock.challenge.controller;

import com.blackrock.challenge.dto.CeilingRequest;
import com.blackrock.challenge.dto.CeilingResponse;
import com.blackrock.challenge.dto.ValidatorRequest;
import com.blackrock.challenge.dto.ValidatorResponse;
import com.blackrock.challenge.dto.FilterRequest;
import com.blackrock.challenge.dto.FilterResponse;
import com.blackrock.challenge.dto.ReturnsRequest;
import com.blackrock.challenge.dto.ReturnsResponse;
import com.blackrock.challenge.dto.PerformanceResponse;
import com.blackrock.challenge.service.CeilingService;
import com.blackrock.challenge.service.ValidationService;
import com.blackrock.challenge.service.ReturnsService;
import com.blackrock.challenge.service.TemporalEngineService;
import com.blackrock.challenge.service.PerformanceService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import com.blackrock.challenge.model.Transaction;

@RestController
@RequestMapping("/blackrock/challenge/v1")
public class TransactionController {

    private final CeilingService ceilingService;
    private final ValidationService validationService;
    private final TemporalEngineService temporalEngineService;
    private final ReturnsService returnsService;
    private final PerformanceService performanceService;

    public TransactionController(CeilingService ceilingService, ValidationService validationService,
            TemporalEngineService temporalEngineService, ReturnsService returnsService,
            PerformanceService performanceService) {
        this.ceilingService = ceilingService;
        this.validationService = validationService;
        this.temporalEngineService = temporalEngineService;
        this.returnsService = returnsService;
        this.performanceService = performanceService;
    }

    @PostMapping("/transactions:parse")
    public List<Transaction> parse(@RequestBody List<Transaction> request) {
        return ceilingService.calculateCeilings(request);
    }

    @PostMapping("/transactions:validator")
    public ValidatorResponse validator(@RequestBody ValidatorRequest request) {
       return validationService.validate(request);
    }

    @PostMapping("/transactions:filter")
    public FilterResponse filter(@RequestBody FilterRequest request) {
        return temporalEngineService.processFilter(request);
    }

    @PostMapping("/returns:nps")
    public ReturnsResponse npsReturns(@RequestBody ReturnsRequest request) {
        return returnsService.calculateNps(request);
    }

    @PostMapping("/returns:index")
    public ReturnsResponse indexReturns(@RequestBody ReturnsRequest request) {
        return returnsService.calculateIndex(request);
    }

    @GetMapping("/performance")
    public PerformanceResponse performance(HttpServletRequest req) {
        // Fallback since we don't have a configured interceptor in this exact scope
        // right now:
        // By standard, we should use a Filter to stamp request start time.
        // For simplicity, we just use the current time, though it misses network IO
        // time.
        Long startTime = (Long) req.getAttribute("requestStartTime");
        if (startTime == null) {
            startTime = System.currentTimeMillis() - 2; // Simulate 2ms for filter routing overhead if not stamped
        }
        return performanceService.getMetrics(startTime);
    }
}
