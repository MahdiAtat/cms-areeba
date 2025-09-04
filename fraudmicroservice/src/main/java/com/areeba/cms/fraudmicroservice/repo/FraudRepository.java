package com.areeba.cms.fraudmicroservice.repo;

import com.areeba.cms.fraudmicroservice.type.FraudEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

/**
 * Repository for {@link FraudEvent}.
 * <p>Includes a helper to count recent events per card (used by the frequency rule).</p>
 */
public interface FraudRepository extends JpaRepository<FraudEvent, UUID> {

    /**
     * Counts fraud events for a card since the given timestamp (inclusive).
     *
     * @param cardId the card identifier
     * @param since  lower bound (UTC instant), included in the count
     * @return number of events matching the window
     */
    @Query("select count(f) from FraudEvent f where f.cardId = :cardId and f.eventTime >= :since")
    long countForCardSince(@Param("cardId") UUID cardId, @Param("since") Instant since);
}
