package com.areeba.cms.cmsmircoservice.cards.repo;

import com.areeba.cms.cmsmircoservice.type.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Repository for {@link Card}.
 * <p>Exposes paged projections of card IDs, optionally filtered by account.</p>
 */
public interface CardRepository extends JpaRepository<Card, UUID> {

    /**
     * Returns a page of card IDs.
     * <p>Use the {@code Pageable} to control page size and sort.</p>
     *
     * @param pageable paging/sorting options
     * @return page of UUIDs
     */
    @Query("select c.id from Card c")
    Page<UUID> findAllIds(Pageable pageable);

    /**
     * Returns a page of card IDs that belong to the given account.
     *
     * @param accountId account identifier
     * @param pageable  paging/sorting options
     * @return page of UUIDs
     */
    @Query("select c.id from Card c where c.account.id = :accountId")
    Page<UUID> findCardIdsByAccountId(@Param("accountId") UUID accountId, Pageable pageable);
}