package com.blackrock.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvalidTransaction extends Transaction {

    @JsonProperty("message")
    private String message;

    public InvalidTransaction() {
        super();
    }

    public InvalidTransaction(Transaction transaction, String message) {
        super(transaction.getDate(), transaction.getAmount());
        this.setCeiling(transaction.getCeiling());
        this.setRemanent(transaction.getRemanent());
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
