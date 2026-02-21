package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.FilterRequest;
import com.blackrock.challenge.dto.FilterResponse;
import com.blackrock.challenge.dto.ValidatorResponse;
import com.blackrock.challenge.model.Period;
import com.blackrock.challenge.model.Transaction;
import com.blackrock.challenge.util.DateParser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TemporalEngineService {

    private final ValidationService validationService;
    private final CeilingService ceilingService;

    public TemporalEngineService(ValidationService validationService, CeilingService ceilingService) {
        this.validationService = validationService;
        this.ceilingService = ceilingService;
    }

    public FilterResponse processFilter(FilterRequest request) {
        // 1. Validate transactions (reuse Epic 3 logic)
        ValidatorResponse valResponse = validationService.validate(request);

        // 2. Enrich with baseline ceiling and remanent (Epic 2)
        List<Transaction> validTrans = ceilingService.calculateCeilings(valResponse.getValid());

        // 3. Apply Q Periods (Fixed Overrides)
        applyQPeriods(validTrans, request.getqPeriods());

        // Remove transactions whose remanent was set to 0 by a Q period override
//        validTrans.removeIf(t -> t.getRemanent() <= 0);

        // 4. Apply P Periods (Extra Additions)
        applyPPeriods(validTrans, request.getpPeriods());

        // 5. Evaluate K Periods
        FilterResponse response = new FilterResponse();
        response.setInvalid(valResponse.getInvalid());

        applyKPeriodsFlag(validTrans, request.getkPeriods());
        response.setValid(validTrans);

        return response;
    }

    private void applyQPeriods(List<Transaction> transactions, List<Period> qPeriods) {
        if (qPeriods == null || qPeriods.isEmpty())
            return;

        for (Transaction t : transactions) {
            LocalDateTime tDate = DateParser.parse(t.getDate());
            Period matchingQ = null;

            for (Period q : qPeriods) {
                LocalDateTime start = DateParser.parse(q.getStart());
                LocalDateTime end = DateParser.parse(q.getEnd());

                // Inclusive check
                if (!tDate.isBefore(start) && !tDate.isAfter(end)) {
                    if (matchingQ == null) {
                        matchingQ = q;
                    } else {
                        // Tie breaker: latest start date. If same start date, keep first found (list
                        // order)
                        LocalDateTime currentMatchingStart = DateParser.parse(matchingQ.getStart());
                        if (start.isAfter(currentMatchingStart)) {
                            matchingQ = q;
                        }
                    }
                }
            }

            if (matchingQ != null) {
                t.setRemanent(matchingQ.getValue()); // Fixed override
            }

        }
    }

    private void applyPPeriods(List<Transaction> transactions, List<Period> pPeriods) {
        if (pPeriods == null || pPeriods.isEmpty())
            return;

        for (Transaction t : transactions) {
            LocalDateTime tDate = DateParser.parse(t.getDate());
            double totalExtra = 0.0;

            for (Period p : pPeriods) {
                LocalDateTime start = DateParser.parse(p.getStart());
                LocalDateTime end = DateParser.parse(p.getEnd());

                // Inclusive check
                if (!tDate.isBefore(start) && !tDate.isAfter(end)) {
                    totalExtra += p.getValue(); // Cumulative additions
                }
            }

            t.setRemanent(t.getRemanent() + totalExtra);
        }
    }

    private void applyKPeriodsFlag(List<Transaction> transactions, List<Period> kPeriods) {
        if (kPeriods == null || kPeriods.isEmpty()) {
            for (Transaction t : transactions) {
                t.setInKPeriod(false);
            }
            return;
        }

        for (Transaction t : transactions) {
            LocalDateTime tDate = DateParser.parse(t.getDate());
            boolean inAnyKPeriod = false;

            for (Period k : kPeriods) {
                LocalDateTime start = DateParser.parse(k.getStart());
                LocalDateTime end = DateParser.parse(k.getEnd());
                if (!tDate.isBefore(start) && !tDate.isAfter(end)) {
                    inAnyKPeriod = true;
                    break;
                }
            }
            t.setInKPeriod(inAnyKPeriod);
        }
    }

    public Map<Period, Double> calculateKPeriodSums(List<Transaction> validTrans, List<Period> kPeriods) {
        Map<Period, Double> kSums = new HashMap<>();
        if (kPeriods == null || kPeriods.isEmpty()) {
            return kSums;
        }

        for (Period k : kPeriods) {
            LocalDateTime start = DateParser.parse(k.getStart());
            LocalDateTime end = DateParser.parse(k.getEnd());
            double sum = 0.0;

            for (Transaction t : validTrans) {
                LocalDateTime tDate = DateParser.parse(t.getDate());
                if (!tDate.isBefore(start) && !tDate.isAfter(end)) {
                    sum += t.getRemanent();
                }
            }
            kSums.put(k, sum);
        }
        return kSums;
    }
}
