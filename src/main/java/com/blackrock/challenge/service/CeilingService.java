package com.blackrock.challenge.service;

import com.blackrock.challenge.model.Transaction;
import com.blackrock.challenge.util.MathUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CeilingService {

    /**
     * Enriches each transaction with ceiling and remanent values.
     * Ceiling = next multiple of 100 (100 → 100, 101 → 200)
     * Remanent = ceiling - amount
     */
    public List<Transaction> calculateCeilings(List<Transaction> transactions) {
        for (Transaction t : transactions) {
            double ceiling = MathUtils.calculateCeiling(t.getAmount());
            double remanent = MathUtils.calculateRemanent(t.getAmount(), ceiling);
            t.setCeiling(ceiling);
            t.setRemanent(remanent);
        }
        return transactions;
    }
}
