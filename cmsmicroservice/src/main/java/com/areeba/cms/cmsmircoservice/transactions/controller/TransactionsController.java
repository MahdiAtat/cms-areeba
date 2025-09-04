package com.areeba.cms.cmsmircoservice.transactions.controller;

import com.areeba.cms.cmsmicroservice.controller.TransactionsApi;
import com.areeba.cms.cmsmicroservice.type.TransactionCreateRequest;
import com.areeba.cms.cmsmicroservice.type.TransactionResponse;
import com.areeba.cms.cmsmircoservice.transactions.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController implements TransactionsApi {

    private static final Logger log = LoggerFactory.getLogger(TransactionsController.class);

    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<TransactionResponse> createTransaction(TransactionCreateRequest transaction) {
        log.debug("Creating transaction {}", transaction);
        TransactionResponse transactionResponse = transactionService.createTransactionService(transaction);
        log.info("Transaction created id={}", transactionResponse.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }
}
