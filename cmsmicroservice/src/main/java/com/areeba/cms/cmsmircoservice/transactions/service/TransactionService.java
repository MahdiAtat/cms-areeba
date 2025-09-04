package com.areeba.cms.cmsmircoservice.transactions.service;

import com.areeba.cms.cmsmicroservice.type.TransactionCreateRequest;
import com.areeba.cms.cmsmicroservice.type.TransactionResponse;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.exception.TransactionRejectedException;

/**
 * Transaction operations.
 */
public interface TransactionService {

    /**
     * Creates a transaction (debit or credit).
     * <p>
     * Flow:
     * <ul>
     *   <li>Validate card/account (ACTIVE, not expired, ownership).</li>
     *   <li>Check sufficient balance for debits.</li>
     *   <li>Run fraud evaluation.</li>
     *   <li>On approval: adjust account balance and persist the transaction.</li>
     *   <li>On rejection: persist a rejected transaction (no balance change).</li>
     * </ul>
     *
     * @param request amount, type (C/D), cardId, accountId, timestamp
     * @return saved transaction view (includes id, final response)
     * @throws ResourceNotFoundException    if card or account is missing
     * @throws TransactionRejectedException if validation fails (inactive/expired/insufficient) or fraud check rejects
     */
    TransactionResponse createTransactionService(TransactionCreateRequest request);

}
