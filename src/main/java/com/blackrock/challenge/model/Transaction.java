package com.blackrock.challenge.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    @JsonProperty("date")
    private String date;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("ceiling")
    private Double ceiling;

    @JsonProperty("remanent")
    private Double remanent;

    @JsonProperty("inKPeriod")
    private Boolean inKPeriod;

    public Transaction() {
    }

    public Transaction(String date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Double getCeiling() {
        return ceiling;
    }

    public void setCeiling(Double ceiling) {
        this.ceiling = ceiling;
    }

    public Double getRemanent() {
        return remanent;
    }

    public void setRemanent(Double remanent) {
        this.remanent = remanent;
    }

    public Boolean getInKPeriod() {
        return inKPeriod;
    }

    public void setInKPeriod(Boolean inKPeriod) {
        this.inKPeriod = inKPeriod;
    }
}
