package com.areeba.cms.fraudmicroservice.service.impl;

import com.areeba.cms.fraudmicroservice.repo.FraudRepository;
import com.areeba.cms.fraudmicroservice.service.FraudService;
import com.areeba.cms.fraudmicroservice.type.FraudCheckRequest;
import com.areeba.cms.fraudmicroservice.type.FraudCheckResponse;
import com.areeba.cms.fraudmicroservice.type.FraudEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class FraudServiceImpl implements FraudService {

    private static final int MAX_TXN_ATTEMPTS = 8;
    private final FraudRepository fraudRepository;
    private final BigDecimal limit;
    private final Duration interval;

    public FraudServiceImpl(FraudRepository fraudRepository,
                            @Value("${fraud.limit}") BigDecimal limit,
                            @Value("${fraud.interval}") Duration interval) {
        this.fraudRepository = fraudRepository;
        this.limit = limit;
        this.interval = interval;
    }

    @Transactional
    @Override
    public FraudCheckResponse evaluateTransactionService(FraudCheckRequest request) {
        BigDecimal amount = request.getAmount();
        boolean amountFlag = amount.compareTo(limit) > 0;
        UUID cardId = request.getCardId();
        OffsetDateTime timeStamp = request.getTimestamp();
        long count = fraudRepository.countForCardSince(cardId, timeStamp.toInstant().minus(interval));
        boolean freqFlag = count >= MAX_TXN_ATTEMPTS;

        // Record the fraud
        FraudEvent fraudEvent = new FraudEvent();
        fraudEvent.setCardId(cardId);
        fraudEvent.setAmount(amount);
        fraudEvent.setEventTime(timeStamp.toInstant());
        fraudRepository.save(fraudEvent);

        FraudCheckResponse fraudCheckResponse = new FraudCheckResponse();

        if (amountFlag || freqFlag) {
            String reason = amountFlag ? "AMOUNT_EXCEEDS_LIMIT" : "FREQUENCY_EXCEEDS_LIMIT";
            fraudCheckResponse.setReason(reason);
            fraudCheckResponse.setApproved(false);
        } else {
            fraudCheckResponse.setReason("OK");
            fraudCheckResponse.setApproved(true);
        }
        return fraudCheckResponse;
    }
}