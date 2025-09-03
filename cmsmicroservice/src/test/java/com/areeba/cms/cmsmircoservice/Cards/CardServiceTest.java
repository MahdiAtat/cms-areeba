package com.areeba.cms.cmsmircoservice.Cards;

import com.areeba.cms.cmsmicroservice.type.*;
import com.areeba.cms.cmsmircoservice.accounts.service.AccountService;
import com.areeba.cms.cmsmircoservice.cards.repo.CardRepository;
import com.areeba.cms.cmsmircoservice.cards.service.impl.CardServiceImpl;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.type.Account;
import com.areeba.cms.cmsmircoservice.type.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    CardRepository cardRepository;

    @Mock
    AccountService accountService;

    @InjectMocks
    CardServiceImpl service;

    @Test
    void createCardService_OK() {
        UUID accountId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        Account acc = new Account();
        acc.setId(accountId);

        when(accountService.requireAccount(accountId)).thenReturn(acc);
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card c = inv.getArgument(0);
            c.setId(cardId);
            return c;
        });

        CardCreateRequest req = new CardCreateRequest();
        req.setAccountId(accountId);
        req.setCardNumber("4111111111111234");
        req.setExpiry(LocalDate.now().plusYears(2));

        CardResponse res = service.createCardService(req);

        assertEquals(cardId, res.getId());
        assertEquals(accountId, res.getAccountId());
        assertEquals(CardStatus.INACTIVE, res.getStatus());
        assertEquals("**** **** **** 1234", res.getMaskedCard());
        assertEquals(req.getExpiry(), res.getExpiry());

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        Card saved = captor.getValue();
        assertSame(acc, saved.getAccount());
        assertEquals("4111111111111234", saved.getCardNumber());
        assertEquals(CardStatus.INACTIVE, saved.getStatus());

        verify(accountService).requireAccount(accountId);
    }

    @Test
    void activateCardService() {
        UUID cardId = UUID.randomUUID();
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.INACTIVE);
        card.setAccount(new Account());

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        service.activateCardService(cardId);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void deactivateCardService() {
        UUID cardId = UUID.randomUUID();
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);
        card.setAccount(new Account());

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        service.deactivateCardService(cardId);

        assertEquals(CardStatus.INACTIVE, card.getStatus());
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void activateCardService_throwsException() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.activateCardService(cardId));

        assertEquals("Card not found", ex.getMessage());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void deactivateCardService_throwsException() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.deactivateCardService(cardId));

        assertEquals("Card not found", ex.getMessage());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardService() {
        UUID cardId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        Account acc = new Account();
        acc.setId(accountId);

        Card card = new Card();
        card.setId(cardId);
        card.setAccount(acc);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiry(LocalDate.now().plusYears(1));
        card.setCardNumber("5555444433332222");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        CardResponse res = service.getCardService(cardId);

        assertEquals(cardId, res.getId());
        assertEquals(accountId, res.getAccountId());
        assertEquals(CardStatus.ACTIVE, res.getStatus());
        assertEquals("**** **** **** 2222", res.getMaskedCard());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardService_throwsException() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getCardService(cardId));
        verify(cardRepository).findById(cardId);
    }


    @Test
    void listCardIdsByAccount() {
        UUID accountId = UUID.randomUUID();
        when(accountService.requireAccount(accountId)).thenReturn(new Account());

        when(cardRepository.findCardIdsByAccountId(eq(accountId), any(Pageable.class)))
                .thenAnswer(inv -> {
                    Pageable pageable = inv.getArgument(1);
                    List<UUID> content = List.of(UUID.randomUUID(), UUID.randomUUID());
                    return new PageImpl<>(content, pageable, 200);
                });

        AccountCardIdsResponse res = service.listCardIdsByAccount(accountId, 3, 500);

        assertEquals(accountId, res.getAccountId());
        assertEquals(2, res.getCardIds().size());
        assertEquals(3, res.getPage());
        assertEquals(50, res.getSize());
        assertEquals(200, res.getTotalElements());
        assertTrue(res.getTotalPages() > 0);
        assertEquals(res.getTotalPages() - 1 > res.getPage(), res.getHasNext());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(cardRepository).findCardIdsByAccountId(eq(accountId), pageableCaptor.capture());
        Pageable used = pageableCaptor.getValue();
        assertEquals(3, used.getPageNumber());
        assertEquals(50, used.getPageSize());

        Sort.Order order = used.getSort().getOrderFor("id");
        assertNotNull(order);
        assertTrue(order.isAscending());

        verify(accountService).requireAccount(accountId);
    }

    @Test
    void listCardIds() {
        when(cardRepository.findAllIds(any(Pageable.class)))
                .thenAnswer(inv -> {
                    Pageable pageable = inv.getArgument(0);
                    List<UUID> content = List.of(UUID.randomUUID());
                    return new PageImpl<>(content, pageable, 123);
                });

        CardIdPage res = service.listCardIds(-1, 999);

        assertEquals(0, res.getPage());
        assertEquals(50, res.getSize());
        assertEquals(1, res.getCardIds().size());
        assertEquals(123, res.getTotalElements());
        assertTrue(res.getTotalPages() > 0);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(cardRepository).findAllIds(pageableCaptor.capture());
        Pageable used = pageableCaptor.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(50, used.getPageSize());
        Sort.Order order = used.getSort().getOrderFor("id");
        assertNotNull(order);
        assertTrue(order.isAscending());
    }
}

