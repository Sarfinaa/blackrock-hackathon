package com.blackrock.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ReturnsResponse {

    @JsonProperty("totalTransactionAmount")
    private double totalTransactionAmount;

    @JsonProperty("totalCeiling")
    private double totalCeiling;

    @JsonProperty("savingsByDates")
    private List<SavingByDate> savingsByDates;

    public ReturnsResponse() {
    }

    public double getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public void setTotalTransactionAmount(double totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }

    public double getTotalCeiling() {
        return totalCeiling;
    }

    public void setTotalCeiling(double totalCeiling) {
        this.totalCeiling = totalCeiling;
    }

    public List<SavingByDate> getSavingsByDates() {
        return savingsByDates;
    }

    public void setSavingsByDates(List<SavingByDate> savingsByDates) {
        this.savingsByDates = savingsByDates;
    }
}
