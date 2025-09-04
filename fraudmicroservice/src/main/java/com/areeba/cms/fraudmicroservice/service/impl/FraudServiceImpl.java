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

/**
 * Fraud rules: amount limit and recent-frequency check.
 * <p>
 * Uses two inputs from config:
 * <ul>
 *   <li><b>fraud.limit</b> — max allowed amount (e.g., 10000.00).</li>
 *   <li><b>fraud.interval</b> — look-back window for frequency (e.g., PT1H).</li>
 * </ul>
 * Counts prior events for the same card within the window and records the current
 * attempt regardless of the decision.
 */
@Service
public class FraudServiceImpl implements FraudService {

    /** Allowed number of attempts within the interval before we start rejecting the next one. */
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

    /**
     * Evaluates a single transaction against the configured rules.
     * <p>
     * Decision logic:
     * <ul>
     *   <li><b>Amount</b>: reject if {@code amount > limit} with reason {@code AMOUNT_EXCEEDS_LIMIT}.</li>
     *   <li><b>Frequency</b>: reject if <i>prior</i> events in {@code [timestamp - interval, timestamp]} ≥ {@code MAX_TXN_ATTEMPTS}
     *       with reason {@code FREQUENCY_EXCEEDS_LIMIT}.</li>
     *   <li>If both trigger, amount reason wins.</li>
     * </ul>
     * Always persists a {@link FraudEvent} for audit (cardId, amount, eventTime).
     *
     * @param request cardId, amount, and event timestamp (UTC offset)
     * @return {@code approved=true} with reason {@code OK} when clean; otherwise {@code approved=false} with the reason
     */
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