package com.areeba.cms.cmsmircoservice.transactions.service;

import com.areeba.cms.cmsmicroservice.type.TransactionCreateRequest;
import com.areeba.cms.cmsmicroservice.type.TransactionResponse;

public interface TransactionService {

    TransactionResponse createTransactionService(TransactionCreateRequest request);

}
