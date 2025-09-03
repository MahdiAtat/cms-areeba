package com.areeba.cms.fraudmicroservice.service;

import com.areeba.cms.fraudmicroservice.type.FraudCheckRequest;
import com.areeba.cms.fraudmicroservice.type.FraudCheckResponse;

public interface FraudService {

    FraudCheckResponse evaluateTransactionService(FraudCheckRequest request);

}
