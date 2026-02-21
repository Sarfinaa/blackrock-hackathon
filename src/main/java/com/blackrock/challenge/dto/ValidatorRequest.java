package com.blackrock.challenge.dto;

import com.blackrock.challenge.model.InvalidTransaction;
import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ValidatorRequest {

    @JsonProperty("transactions")
    private List<Transaction> transactions;

    @JsonProperty("wage")
    private double wage;

    @JsonProperty("maxInvestment")
    private double maxInvestment;

    public ValidatorRequest() {
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public double getMaxInvestment() {
        return maxInvestment;
    }

    public void setMaxInvestment(double maxInvestment) {
        this.maxInvestment = maxInvestment;
    }
}
