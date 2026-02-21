package com.blackrock.challenge.dto;

import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CeilingRequest {

    @JsonProperty("transactions")
    private List<Transaction> transactions;

    public CeilingRequest() {
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
