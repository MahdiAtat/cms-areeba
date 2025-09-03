package com.areeba.cms.fraudmicroservice.controller;

import com.areeba.cms.fraudmicroservice.service.FraudService;
import com.areeba.cms.fraudmicroservice.type.FraudCheckRequest;
import com.areeba.cms.fraudmicroservice.type.FraudCheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FraudController implements EvaluateApi {

    private final FraudService fraudService;

    public FraudController(FraudService fraudService) {
        this.fraudService = fraudService;
    }

    @Override
    public ResponseEntity<FraudCheckResponse> evaluateTransaction(FraudCheckRequest fraudCheckRequest) {
        return ResponseEntity.ok(fraudService.evaluateTransactionService(fraudCheckRequest));
    }
}
