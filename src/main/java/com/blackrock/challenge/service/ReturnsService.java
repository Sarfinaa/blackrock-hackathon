package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.ReturnsRequest;
import com.blackrock.challenge.dto.ReturnsResponse;
import com.blackrock.challenge.util.MathUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReturnsService {

    public static final double NPS_ANNUAL_RATE = 0.0711; // 7.11%
    public static final double INDEX_ANNUAL_RATE = 0.1449; // 14.49%

    private final ValidationService validationService;
    private final TemporalEngineService temporalEngineService;

    public ReturnsService(ValidationService validationService, TemporalEngineService temporalEngineService) {
        this.validationService = validationService;
        this.temporalEngineService = temporalEngineService;
    }

    /**
     * Calculates NPS returns including compound interest, inflation adjustment, and
     * tax benefit.
     */
    public ReturnsResponse calculateNps(ReturnsRequest request) {
        return calculateReturns(request, NPS_ANNUAL_RATE, true);
    }

    /**
     * Calculates Index Fund returns (no tax benefit).
     */
    public ReturnsResponse calculateIndex(ReturnsRequest request) {
        return calculateReturns(request, INDEX_ANNUAL_RATE, false);
    }

    private ReturnsResponse calculateReturns(ReturnsRequest request, double annualRate, boolean applyTaxBenefit) {
        ReturnsResponse response = new ReturnsResponse();

        // 1. Validate and Filter transactions to get the basic valid list
        com.blackrock.challenge.dto.FilterResponse filterResponse = temporalEngineService.processFilter(request);
        List<com.blackrock.challenge.model.Transaction> validTrans = filterResponse.getValid();

        if (validTrans == null || validTrans.isEmpty()) {
            response.setSavingsByDates(new ArrayList<>());
            response.setTotalTransactionAmount(0.0);
            response.setTotalCeiling(0.0);
            return response;
        }

        // Calculate Global Totals
        double totalTransactionAmount = validTrans.stream()
                .mapToDouble(com.blackrock.challenge.model.Transaction::getAmount).sum();
        double totalCeiling = validTrans.stream().mapToDouble(com.blackrock.challenge.model.Transaction::getCeiling)
                .sum();

        // 2. Map Valid Transactions to specific K periods to get the remanent sums
        Map<com.blackrock.challenge.model.Period, Double> sumsPerK = temporalEngineService
                .calculateKPeriodSums(validTrans, request.getkPeriods());

        int investmentYears = Math.max(60 - request.getAge(), 5);
        List<com.blackrock.challenge.dto.SavingByDate> savingsByDates = new ArrayList<>();

        for (Map.Entry<com.blackrock.challenge.model.Period, Double> entry : sumsPerK.entrySet()) {
            com.blackrock.challenge.dto.SavingByDate saving = new com.blackrock.challenge.dto.SavingByDate();
            saving.setStart(entry.getKey().getStart());
            saving.setEnd(entry.getKey().getEnd());

            double principal = entry.getValue();
            saving.setAmount(principal);

            // Calculate Compound Interest
            double futureValue = MathUtils.compoundInterest(principal, annualRate, investmentYears);

            // Inflation adjustment (normalize if expressed as percentage, e.g. 5.5 → 0.055)
            double inflationRate = request.getInflationRate();
            if (inflationRate > 1)
                inflationRate = inflationRate / 100.0;
            double inflationDivisor = Math.pow(1 + inflationRate, investmentYears);
            double adjustedValue = futureValue / inflationDivisor;

            // Round profit
            saving.setProfit(Math.round((adjustedValue - principal) * 100.0) / 100.0);

            // Tax Benefit applies only to NPS
            if (applyTaxBenefit) {
                // wage is monthly; annual income = wage × 12 (per challenge spec)
                saving.setTaxBenefit(calculateTaxBenefit(principal, request.getWage() * 12));
            } else {
                saving.setTaxBenefit(0.0);
            }

            savingsByDates.add(saving);
        }

        response.setSavingsByDates(savingsByDates);
        response.setTotalTransactionAmount(totalTransactionAmount);
        response.setTotalCeiling(totalCeiling);

        return response;
    }

    /**
     * Tax Benefit = Tax(annual_income) - Tax(annual_income - NPS_Deduction)
     *
     * NPS_Deduction = min(invested, 10% of annual_income, ₹2,00,000)
     *
     * Tax Slabs (Simplified):
     * ₹0-7L → 0%
     * ₹7L-10L → 10% on amount above ₹7L
     * ₹10L-12L → 15% on amount above ₹10L
     * ₹12L-15L → 20% on amount above ₹12L
     * >₹15L → 30% on amount above ₹15L
     */
    private double calculateTaxBenefit(double invested, double annualIncome) {
        double npsDeduction = Math.min(invested, Math.min(annualIncome * 0.10, 200000.0));
        double taxOnIncome = calculateTax(annualIncome);
        double taxOnReduced = calculateTax(annualIncome - npsDeduction);
        return Math.max(0, taxOnIncome - taxOnReduced);
    }

    private double calculateTax(double income) {
        if (income <= 700000)
            return 0;
        double tax = 0;
        if (income > 1500000) {
            tax += (income - 1500000) * 0.30;
            income = 1500000;
        }
        if (income > 1200000) {
            tax += (income - 1200000) * 0.20;
            income = 1200000;
        }
        if (income > 1000000) {
            tax += (income - 1000000) * 0.15;
            income = 1000000;
        }
        if (income > 700000) {
            tax += (income - 700000) * 0.10;
        }
        return tax;
    }
}
