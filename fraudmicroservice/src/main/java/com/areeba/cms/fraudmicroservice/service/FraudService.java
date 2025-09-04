package com.areeba.cms.fraudmicroservice.service;

import com.areeba.cms.fraudmicroservice.type.FraudCheckRequest;
import com.areeba.cms.fraudmicroservice.type.FraudCheckResponse;

/**
 * Fraud evaluation contract.
 * <p>
 * Given a transaction snapshot (cardId, amount, timestamp), returns a decision
 * indicating whether it’s approved and why.
 * </p>
 */
public interface FraudService {

    /**
     * Runs fraud checks (e.g., amount limit and recent frequency).
     *
     * @param request cardId, amount, and event timestamp
     * @return decision with {@code approved} flag and a {@code reason}
     */
    FraudCheckResponse evaluateTransactionService(FraudCheckRequest request);

}
