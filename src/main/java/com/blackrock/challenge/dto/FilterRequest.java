package com.blackrock.challenge.dto;

import com.blackrock.challenge.model.Period;
import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FilterRequest extends ValidatorRequest {

    // Inherits transactions, wage, maxInvestment from ValidatorRequest

    @JsonProperty("q")
    private List<Period> qPeriods;

    @JsonProperty("p")
    private List<Period> pPeriods;

    @JsonProperty("k")
    private List<Period> kPeriods;

    public FilterRequest() {
    }

    public List<Period> getqPeriods() {
        return qPeriods;
    }

    public void setqPeriods(List<Period> qPeriods) {
        this.qPeriods = qPeriods;
    }

    public List<Period> getpPeriods() {
        return pPeriods;
    }

    public void setpPeriods(List<Period> pPeriods) {
        this.pPeriods = pPeriods;
    }

    public List<Period> getkPeriods() {
        return kPeriods;
    }

    public void setkPeriods(List<Period> kPeriods) {
        this.kPeriods = kPeriods;
    }
}
