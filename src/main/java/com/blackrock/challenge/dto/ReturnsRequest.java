package com.blackrock.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReturnsRequest extends FilterRequest {

    @JsonProperty("age")
    private int age;

    @JsonProperty("inflation")
    private double inflationRate;

    public ReturnsRequest() {
        super();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(double inflationRate) {
        this.inflationRate = inflationRate;
    }
}
