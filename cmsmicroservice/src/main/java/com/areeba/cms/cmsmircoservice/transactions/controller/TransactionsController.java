package com.areeba.cms.cmsmircoservice.transactions.controller;

import com.areeba.cms.cmsmicroservice.controller.TransactionsApi;
import com.areeba.cms.cmsmicroservice.type.TransactionCreateRequest;
import com.areeba.cms.cmsmicroservice.type.TransactionResponse;
import com.areeba.cms.cmsmircoservice.transactions.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController implements TransactionsApi {

    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<TransactionResponse> createTransaction(TransactionCreateRequest transaction) {
        return ResponseEntity.ok(transactionService.createTransactionService(transaction));
    }
}
