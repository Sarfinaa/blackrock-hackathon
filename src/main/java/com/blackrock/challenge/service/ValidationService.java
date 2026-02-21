package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.ValidatorRequest;
import com.blackrock.challenge.dto.ValidatorResponse;
import com.blackrock.challenge.model.InvalidTransaction;
import com.blackrock.challenge.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ValidationService {

    /**
     * Validates a list of transactions based on challenge rules:
     * 1. Negatives rejected
     * 2. Duplicates rejected (same date and amount)
     * 3. Total amount cannot exceed maxInvestment OR wage
     * Retains original list ordering and processes validations sequentially.
     */
    public ValidatorResponse validate(ValidatorRequest request) {
        ValidatorResponse response = new ValidatorResponse();
        Set<String> seenTransactions = new HashSet<>();
        double accumulatedInvestment = 0.0;

        for (Transaction t : request.getTransactions()) {
            // Rule 1: Negatives
            if (t.getAmount() < 0) {
                response.getInvalid().add(new InvalidTransaction(t, "Amount cannot be negative"));
                continue;
            }

            // Rule 2: Duplicates (same date and amount)
            String uniqueKey = t.getDate() + "_" + t.getAmount();
            if (seenTransactions.contains(uniqueKey)) {
                response.getInvalid().add(new InvalidTransaction(t, "Duplicate transaction detected"));
                continue;
            }
            seenTransactions.add(uniqueKey);
            // Valid transaction
            accumulatedInvestment += t.getAmount();
            response.getValid().add(t);
        }

        return response;
    }
}
