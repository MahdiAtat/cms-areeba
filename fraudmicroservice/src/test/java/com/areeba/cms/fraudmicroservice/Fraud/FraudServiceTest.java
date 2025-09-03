package com.areeba.cms.fraudmicroservice.Fraud;

import com.areeba.cms.fraudmicroservice.repo.FraudRepository;
import com.areeba.cms.fraudmicroservice.service.impl.FraudServiceImpl;
import com.areeba.cms.fraudmicroservice.type.FraudCheckRequest;
import com.areeba.cms.fraudmicroservice.type.FraudCheckResponse;
import com.areeba.cms.fraudmicroservice.type.FraudEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FraudServiceTest {

    @Mock
    FraudRepository fraudRepository;

    FraudServiceImpl service;

    private static final BigDecimal LIMIT = new BigDecimal("10000.00");
    private static final Duration INTERVAL = Duration.ofHours(1);

    @BeforeEach
    void setUp() {
        service = new FraudServiceImpl(fraudRepository, LIMIT, INTERVAL);
    }

    private FraudCheckRequest req(UUID cardId, BigDecimal amount, String isoTs) {
        FraudCheckRequest r = new FraudCheckRequest();
        r.setCardId(cardId);
        r.setAmount(amount);
        r.setTimestamp(OffsetDateTime.parse(isoTs));
        return r;
    }

    @Test
    void approve_whenAmountBelowLimit_andFrequencyUnderThreshold() {
        UUID cardId = UUID.randomUUID();
        var request = req(cardId, new BigDecimal("9999.99"), "2025-09-02T09:00:00Z");
        // Count for last hour = 3 (< 8)
        when(fraudRepository.countForCardSince(eq(cardId), any(Instant.class))).thenReturn(3L);
        when(fraudRepository.save(any(FraudEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        FraudCheckResponse resp = service.evaluateTransactionService(request);

        assertTrue(resp.getApproved());
        assertEquals("OK", resp.getReason());

        // Verify "since" = timestamp - interval
        ArgumentCaptor<Instant> sinceCap = ArgumentCaptor.forClass(Instant.class);
        verify(fraudRepository).countForCardSince(eq(cardId), sinceCap.capture());
        assertEquals(request.getTimestamp().toInstant().minus(INTERVAL), sinceCap.getValue());

        // Verify event saved with right fields
        ArgumentCaptor<FraudEvent> eventCap = ArgumentCaptor.forClass(FraudEvent.class);
        verify(fraudRepository).save(eventCap.capture());
        FraudEvent saved = eventCap.getValue();
        assertEquals(cardId, saved.getCardId());
        assertEquals(0, saved.getAmount().compareTo(new BigDecimal("9999.99")));
        assertEquals(request.getTimestamp().toInstant(), saved.getEventTime());
    }

    @Test
    void reject_whenAmountExceedsLimit_reasonAmount() {
        UUID cardId = UUID.randomUUID();
        var request = req(cardId, new BigDecimal("15000.00"), "2025-09-02T10:00:00Z");
        when(fraudRepository.countForCardSince(eq(cardId), any(Instant.class))).thenReturn(0L);
        when(fraudRepository.save(any(FraudEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        FraudCheckResponse resp = service.evaluateTransactionService(request);

        assertFalse(resp.getApproved());
        assertEquals("AMOUNT_EXCEEDS_LIMIT", resp.getReason());
        verify(fraudRepository).countForCardSince(eq(cardId), any(Instant.class));
        verify(fraudRepository).save(any(FraudEvent.class));
    }

    @Test
    void reject_whenFrequencyAtOrAboveThreshold_reasonFrequency() {
        UUID cardId = UUID.randomUUID();
        var request = req(cardId, new BigDecimal("50.00"), "2025-09-02T11:00:00Z");
        // count >= 8 -> reject by frequency
        when(fraudRepository.countForCardSince(eq(cardId), any(Instant.class))).thenReturn(8L);
        when(fraudRepository.save(any(FraudEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        FraudCheckResponse resp = service.evaluateTransactionService(request);

        assertFalse(resp.getApproved());
        assertEquals("FREQUENCY_EXCEEDS_LIMIT", resp.getReason());
        verify(fraudRepository).countForCardSince(eq(cardId), any(Instant.class));
        verify(fraudRepository).save(any(FraudEvent.class));
    }

    @Test
    void edge_amountEqualToLimit_isApproved() {
        UUID cardId = UUID.randomUUID();
        var request = req(cardId, new BigDecimal("10000.00"), "2025-09-02T13:00:00Z");
        when(fraudRepository.countForCardSince(eq(cardId), any(Instant.class))).thenReturn(7L);
        when(fraudRepository.save(any(FraudEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        FraudCheckResponse resp = service.evaluateTransactionService(request);

        assertTrue(resp.getApproved());
        assertEquals("OK", resp.getReason());
    }
}
