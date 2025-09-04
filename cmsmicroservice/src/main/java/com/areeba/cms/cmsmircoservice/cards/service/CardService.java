package com.areeba.cms.cmsmircoservice.cards.service;

import com.areeba.cms.cmsmicroservice.type.AccountCardIdsResponse;
import com.areeba.cms.cmsmicroservice.type.CardCreateRequest;
import com.areeba.cms.cmsmicroservice.type.CardIdPage;
import com.areeba.cms.cmsmicroservice.type.CardResponse;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;

import java.util.UUID;

/**
 * Card operations: create, activate/deactivate, read, and list IDs.
 */
public interface CardService {

    /**
     * Creates a card under the given account.
     *
     * @param req account id, PAN, expiry
     * @return created card view
     */
    CardResponse createCardService(CardCreateRequest req);

    /**
     * Sets card status to ACTIVE.
     *
     * @param id card id
     * @throws ResourceNotFoundException if not found
     */
    void activateCardService(UUID id);

    /**
     * Sets card status to INACTIVE.
     *
     * @param id card id
     * @throws ResourceNotFoundException if not found
     */
    void deactivateCardService(UUID id);

    /**
     * Returns card details (masked PAN).
     *
     * @param id card id
     * @return card view
     * @throws ResourceNotFoundException if not found
     */
    CardResponse getCardService(UUID id);

    /**
     * Lists card IDs across the system (paged).
     * <p>Service may cap the page size</p>
     *
     * @param page zero-based page index
     * @param size requested page size
     * @return page of card IDs with paging metadata
     */
    CardIdPage listCardIds(int page, int size);

    /**
     * Lists card IDs for a specific account (paged).
     * <p>Service may cap the page size</p>
     *
     * @param accountId account id
     * @param page      zero-based page index
     * @param size      requested page size
     * @return page of card IDs for that account with paging metadata
     * @throws ResourceNotFoundException if the account doesn’t exist
     */
    AccountCardIdsResponse listCardIdsByAccount(UUID accountId, int page, int size);
}