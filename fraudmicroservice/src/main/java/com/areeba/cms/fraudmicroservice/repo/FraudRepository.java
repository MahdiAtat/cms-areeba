package com.areeba.cms.fraudmicroservice.repo;

import com.areeba.cms.fraudmicroservice.type.FraudEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;


public interface FraudRepository extends JpaRepository<FraudEvent, UUID> {
    @Query("select count(f) from FraudEvent f where f.cardId = :cardId and f.eventTime >= :since")
    long countForCardSince(@Param("cardId") UUID cardId, @Param("since") Instant since);
}
