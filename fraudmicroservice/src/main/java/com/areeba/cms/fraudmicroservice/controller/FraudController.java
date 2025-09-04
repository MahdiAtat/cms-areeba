package com.areeba.cms.fraudmicroservice.controller;

import com.areeba.cms.fraudmicroservice.service.FraudService;
import com.areeba.cms.fraudmicroservice.type.FraudCheckRequest;
import com.areeba.cms.fraudmicroservice.type.FraudCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FraudController implements EvaluateApi {

    private static final Logger log = LoggerFactory.getLogger(FraudController.class);

    private final FraudService fraudService;

    public FraudController(FraudService fraudService) {
        this.fraudService = fraudService;
    }

    @Override
    public ResponseEntity<FraudCheckResponse> evaluateTransaction(FraudCheckRequest fraudCheckRequest) {
        log.debug("Evaluate transaction for cardId {}", fraudCheckRequest.getCardId());
        FraudCheckResponse fraudCheckResponse = fraudService.evaluateTransactionService(fraudCheckRequest);
        log.info("Transaction evaluated");
        return ResponseEntity.ok(fraudCheckResponse);
    }
}
