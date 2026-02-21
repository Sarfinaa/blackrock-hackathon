package com.blackrock.challenge.util;

public class MathUtils {

    private MathUtils() {
    }

    /**
     * Calculates the ceiling (next multiple of 100) for a given amount.
     * If the amount is already a multiple of 100, returns the amount itself.
     * Examples: 100 → 100, 101 → 200, 250 → 300, 0 → 0
     */
    public static double calculateCeiling(double amount) {
        double ceiling = Math.ceil(amount / 100.0) * 100.0;
        return ceiling;
    }

    /**
     * Calculates the remanent (ceiling - amount).
     */
    public static double calculateRemanent(double amount, double ceiling) {
        return ceiling - amount;
    }

    /**
     * Calculates compound interest: principal * (1 + rate)^years
     */
    public static double compoundInterest(double principal, double annualRate, int years) {
        return principal * Math.pow(1 + annualRate, years);
    }
}
