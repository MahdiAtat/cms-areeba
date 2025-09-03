package com.areeba.cms.cmsmircoservice.cards.repo;

import com.areeba.cms.cmsmircoservice.type.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;


public interface CardRepository extends JpaRepository<Card, UUID> {

    @Query("select c.id from Card c")
    Page<UUID> findAllIds(Pageable pageable);

    @Query("select c.id from Card c where c.account.id = :accountId")
    Page<UUID> findCardIdsByAccountId(@Param("accountId") UUID accountId, Pageable pageable);
}
