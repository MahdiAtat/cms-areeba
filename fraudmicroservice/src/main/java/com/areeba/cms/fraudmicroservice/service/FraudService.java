package com.areeba.cms.fraudmicroservice.service;

import com.areeba.cms.cmsmicroservice.type.TransactionCreateRequest;
import com.areeba.cms.cmsmicroservice.type.TransactionResponse;

public interface TransactionService {

    TransactionResponse createTransactionService(TransactionCreateRequest request);

}
