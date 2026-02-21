package com.blackrock.challenge;

import com.blackrock.challenge.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Transaction t = new Transaction();
        t.setDate("2023-02-28 15:49:20");
        t.setAmount(375.0);
        t.setCeiling(400.0);
        t.setRemanent(25.0);
        t.setInKPeriod(true);
        System.out.println("JSON: " + mapper.writeValueAsString(t));
    }
}
