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

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;

    public CardServiceImpl(CardRepository cardRepository, AccountService accountService) {
        this.cardRepository = cardRepository;
        this.accountService = accountService;
    }

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

    @Transactional
    @Override
    public void activateCardService(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(CardStatus.ACTIVE);
    }

    @Transactional
    @Override
    public void deactivateCardService(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(CardStatus.INACTIVE);
    }

    @Transactional(readOnly = true)
    @Override
    public CardResponse getCardService(UUID id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return toResponse(card);
    }

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