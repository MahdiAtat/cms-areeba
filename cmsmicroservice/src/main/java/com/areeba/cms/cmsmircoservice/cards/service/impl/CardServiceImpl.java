package com.areeba.cms.cmsmircoservice.cards.service.impl;

import com.areeba.cms.cmsmicroservice.type.*;
import com.areeba.cms.cmsmircoservice.accounts.service.AccountService;
import com.areeba.cms.cmsmircoservice.cards.repo.CardRepository;
import com.areeba.cms.cmsmircoservice.cards.service.CardService;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.type.Account;
import com.areeba.cms.cmsmircoservice.type.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Card service: create, activate/deactivate, read, and list card IDs.
 * <p>Write methods run in a transaction; status/field changes are flushed by JPA dirty checking.</p>
 * <p>Responses include a masked PAN (last 4 digits only).</p>
 */
@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;

    public CardServiceImpl(CardRepository cardRepository, AccountService accountService) {
        this.cardRepository = cardRepository;
        this.accountService = accountService;
    }

    /**
     * Creates a new card under the target account.
     * <p>New cards start as {@code INACTIVE}.</p>
     *
     * @param cardCreateRequest account id, raw PAN, expiry
     * @return created card view (masked PAN, generated id)
     * @throws ResourceNotFoundException if the account does not exist
     */
    @Transactional
    @Override
    public CardResponse createCardService(CardCreateRequest cardCreateRequest) {
        Account account = accountService.requireAccount(cardCreateRequest.getAccountId());
        Card card = new Card();
        card.setAccount(account);
        card.setCardNumber(cardCreateRequest.getCardNumber());
        card.setExpiry(cardCreateRequest.getExpiry());
        card.setStatus(CardStatus.INACTIVE);
        card = cardRepository.save(card);
        return toResponse(card);
    }

    /**
     * Turns the card status to {@code ACTIVE}.
     *
     * @param id card id
     * @throws ResourceNotFoundException if the card does not exist
     */
    @Transactional
    @Override
    public void activateCardService(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(CardStatus.ACTIVE);
    }

    /**
     * Turns the card status to {@code INACTIVE}.
     *
     * @param id card id
     * @throws ResourceNotFoundException if the card does not exist
     */
    @Transactional
    @Override
    public void deactivateCardService(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(CardStatus.INACTIVE);
    }

    /**
     * Returns a single card view (masked PAN).
     *
     * @param id card id
     * @return card details
     * @throws ResourceNotFoundException if the card does not exist
     */
    @Transactional(readOnly = true)
    @Override
    public CardResponse getCardService(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return toResponse(card);
    }

    /**
     * Maps a {@link Card} entity to its API response.
     * <p>Masking rule: show only the last 4 digits, or {@code "****"} if missing/too short.</p>
     *
     * @param card managed entity
     * @return response DTO with masked PAN
     */
    private CardResponse toResponse(Card card) {
        String plain = card.getCardNumber();
        String masked = (plain == null || plain.length() < 4) ? "****" : "**** **** **** " + plain.substring(plain.length() - 4);
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(card.getId());
        cardResponse.setAccountId(card.getAccount().getId());
        cardResponse.setStatus(card.getStatus());
        cardResponse.setExpiry(card.getExpiry());
        cardResponse.setMaskedCard(masked);
        return cardResponse;
    }

    /**
     * Lists card IDs for a specific account (paged).
     * <p>Page index is normalized to {@code >= 0}; size is capped at {@code 50}.</p>
     *
     * @param accountId account id
     * @param page      zero-based page index
     * @param size      requested page size (server caps at 50)
     * @return page of card IDs with paging metadata
     * @throws ResourceNotFoundException if the account does not exist
     */
    @Transactional(readOnly = true)
    @Override
    public AccountCardIdsResponse listCardIdsByAccount(UUID accountId, int page, int size) {
        accountService.requireAccount(accountId);
        int p = Math.max(0, page);
        int s = Math.min(Math.max(size, 1), 50);
        Page<UUID> result = cardRepository.findCardIdsByAccountId(accountId, PageRequest.of(p, s, Sort.by("id").ascending()));
        AccountCardIdsResponse accountCardIdsResponse = new AccountCardIdsResponse();
        accountCardIdsResponse.setCardIds(result.getContent());
        accountCardIdsResponse.setAccountId(accountId);
        accountCardIdsResponse.setPage(result.getNumber());
        accountCardIdsResponse.setSize(result.getSize());
        accountCardIdsResponse.setTotalElements(result.getTotalElements());
        accountCardIdsResponse.setTotalPages(result.getTotalPages());
        accountCardIdsResponse.setHasNext(result.hasNext());
        return accountCardIdsResponse;
    }

    /**
     * Lists all card IDs (paged).
     * <p>Page index is normalized to {@code >= 0}; size is capped at {@code 50}.</p>
     *
     * @param page zero-based page index
     * @param size requested page size (server caps at 50)
     * @return page of card IDs with paging metadata
     */
    @Transactional(readOnly = true)
    @Override
    public CardIdPage listCardIds(int page, int size) {
        int p = Math.max(0, page);
        int s = Math.min(Math.max(size, 1), 50);
        Page<UUID> result = cardRepository.findAllIds(PageRequest.of(p, s, Sort.by("id").ascending()));
        CardIdPage cardIdPage = new CardIdPage();
        cardIdPage.setCardIds(result.getContent());
        cardIdPage.setPage(result.getNumber());
        cardIdPage.setSize(result.getSize());
        cardIdPage.setTotalPages(result.getTotalPages());
        cardIdPage.setTotalElements(result.getTotalElements());
        cardIdPage.setHasNext(result.hasNext());
        return cardIdPage;
    }
}