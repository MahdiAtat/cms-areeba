package com.areeba.cms.cmsmircoservice.Cards.Transactions;

import com.areeba.cms.cmsmicroservice.type.*;
import com.areeba.cms.cmsmircoservice.accounts.repo.AccountRepository;
import com.areeba.cms.cmsmircoservice.cards.repo.CardRepository;
import com.areeba.cms.cmsmircoservice.exception.TransactionRejectedException;
import com.areeba.cms.cmsmircoservice.rest.FraudClient;
import com.areeba.cms.cmsmircoservice.transactions.repo.TransactionRepository;
import com.areeba.cms.cmsmircoservice.transactions.service.impl.TransactionServiceImpl;
import com.areeba.cms.cmsmircoservice.type.Account;
import com.areeba.cms.cmsmircoservice.type.Card;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    AccountRepository accountRepository;
    @Mock
    CardRepository cardRepository;
    @Mock
    FraudClient fraudClient;
    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;


    @Test
    void debitApprovedAndBalanceReduced() {

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("500.00"));
        when(accountRepository.findByIdForUpdate(account.getId())).thenReturn(Optional.of(account));

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setAccount(account);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiry(LocalDate.now().plusYears(1));
        card.setCardNumber("4111");
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        FraudCheckResponse fraudCheckResponse = new FraudCheckResponse();
        fraudCheckResponse.setApproved(true);
        fraudCheckResponse.setReason("OK");
        when(fraudClient.evaluate(any(FraudCheckRequest.class))).thenReturn(fraudCheckResponse);

        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest();
        transactionCreateRequest.setAccountId(account.getId());
        transactionCreateRequest.setCardId(card.getId());
        transactionCreateRequest.setTransactionAmount(new BigDecimal("100.00"));
        transactionCreateRequest.setTransactionType(TransactionType.D);

        TransactionResponse result = transactionService.createTransactionService(transactionCreateRequest);
        assertEquals(TransactionResponse.ResponseEnum.APPROVED, result.getResponse());
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("400.00")));
    }

    @Test
    void creditApprovedAndBalanceIncreased() {

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("500.00"));
        when(accountRepository.findByIdForUpdate(account.getId())).thenReturn(Optional.of(account));

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setAccount(account);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiry(LocalDate.now().plusYears(1));
        card.setCardNumber("4111");
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        FraudCheckResponse fraudCheckResponse = new FraudCheckResponse();
        fraudCheckResponse.setApproved(true);
        fraudCheckResponse.setReason("OK");
        when(fraudClient.evaluate(any(FraudCheckRequest.class))).thenReturn(fraudCheckResponse);

        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest();
        transactionCreateRequest.setAccountId(account.getId());
        transactionCreateRequest.setCardId(card.getId());
        transactionCreateRequest.setTransactionAmount(new BigDecimal("100.00"));
        transactionCreateRequest.setTransactionType(TransactionType.C);

        TransactionResponse result = transactionService.createTransactionService(transactionCreateRequest);
        assertEquals(TransactionResponse.ResponseEnum.APPROVED, result.getResponse());
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("600.00")));
    }

    @Test
    void debitDeclinedInsufficientBalance() {

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("0"));
        when(accountRepository.findByIdForUpdate(account.getId())).thenReturn(Optional.of(account));

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setAccount(account);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiry(LocalDate.now().plusYears(1));
        card.setCardNumber("4111");
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest();
        transactionCreateRequest.setAccountId(account.getId());
        transactionCreateRequest.setCardId(card.getId());
        transactionCreateRequest.setTransactionAmount(new BigDecimal("100.00"));
        transactionCreateRequest.setTransactionType(TransactionType.D);

        TransactionRejectedException ex = assertThrows(
                TransactionRejectedException.class,
                () -> transactionService.createTransactionService(transactionCreateRequest)
        );

        assertEquals("Insufficient balance", ex.getMessage());


        verifyNoInteractions(fraudClient);
        verify(transactionRepository, never()).save(any());
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("0")));
    }

    @Test
    void debitDeclinedCardNotActive() {

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("1000"));
        when(accountRepository.findByIdForUpdate(account.getId())).thenReturn(Optional.of(account));

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setAccount(account);
        card.setStatus(CardStatus.INACTIVE);
        card.setExpiry(LocalDate.now().plusYears(1));
        card.setCardNumber("4111");
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        TransactionCreateRequest transactionCreateRequest = new TransactionCreateRequest();
        transactionCreateRequest.setAccountId(account.getId());
        transactionCreateRequest.setCardId(card.getId());
        transactionCreateRequest.setTransactionAmount(new BigDecimal("100.00"));
        transactionCreateRequest.setTransactionType(TransactionType.D);

        TransactionRejectedException ex = assertThrows(
                TransactionRejectedException.class,
                () -> transactionService.createTransactionService(transactionCreateRequest)
        );

        assertEquals("Card not active", ex.getMessage());


        verifyNoInteractions(fraudClient);
        verify(transactionRepository, never()).save(any());
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("1000")));
    }

}
