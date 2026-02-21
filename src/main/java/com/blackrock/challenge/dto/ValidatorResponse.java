package com.blackrock.challenge.dto;

import com.blackrock.challenge.model.InvalidTransaction;
import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ValidatorResponse {

    @JsonProperty("valid")
    private List<Transaction> valid = new ArrayList<>();

    @JsonProperty("invalid")
    private List<InvalidTransaction> invalid = new ArrayList<>();

    public ValidatorResponse() {
    }

    public List<Transaction> getValid() {
        return valid;
    }

    public void setValid(List<Transaction> valid) {
        this.valid = valid;
    }

    public List<InvalidTransaction> getInvalid() {
        return invalid;
    }

    public void setInvalid(List<InvalidTransaction> invalid) {
        this.invalid = invalid;
    }
}
