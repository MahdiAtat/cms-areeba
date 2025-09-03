package com.areeba.cms.fraudmicroservice.service.impl;

import com.areeba.cms.cmsmircoservice.accounts.repo.AccountRepository;
import com.areeba.cms.cmsmircoservice.cards.repo.CardRepository;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.exception.TransactionRejectedException;
import com.areeba.cms.cmsmircoservice.transactions.repo.TransactionRepository;
import com.areeba.cms.cmsmircoservice.transactions.service.TransactionService;
import com.areeba.cms.cmsmircoservice.type.Account;
import com.areeba.cms.cmsmircoservice.type.Card;
import com.areeba.cms.cmsmircoservice.type.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
    }


    @Transactional
    @Override
    public TransactionResponse createTransactionService(TransactionCreateRequest request) {
        // Load and lock account for update
        Account account = accountRepository.findByIdForUpdate(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Card eligibility
        if (card.getStatus() != CardStatus.ACTIVE)
            throw new TransactionRejectedException("Card not active");
        if (card.getExpiry().isBefore(LocalDate.now()))
            throw new TransactionRejectedException("Card expired");
        if (!card.getAccount().getId().equals(account.getId()))
            throw new TransactionRejectedException("Card does not belong to account");

        // Account eligibility
        if (account.getStatus() != AccountStatus.ACTIVE)
            throw new TransactionRejectedException("Account not active");
        BigDecimal amount = request.getTransactionAmount();
        if (request.getTransactionType() == TransactionType.D && account.getBalance().compareTo(amount) < 0)
            throw new TransactionRejectedException("Insufficient balance");

//        // Fraud check
//        FraudCheckResponse fraud = fraudClient.evaluate(
//                new FraudCheckRequest(card.getId(), amount, Instant.now())
//        );
//        if (!fraud.approved()) {
//            // Save rejected transaction record (no balance change)
//            Transaction rejected = new Transaction();
//            rejected.setAccount(account); rejected.setCard(card);
//            rejected.setTransactionAmount(amount); rejected.setTransactionType(request.transactionType());
//            rejected.setTransactionDate(Instant.now()); rejected.setResponse("REJECTED");
//            rejected = transactionRepository.save(rejected);
//            return toResponse(rejected);
//        }

        // Apply balance
        if (request.getTransactionType() == TransactionType.D) {
            account.setBalance(account.getBalance().subtract(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
        }

        // Persist transaction (approved)
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCard(card);
        transaction.setTransactionAmount(amount);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionDate(Instant.now());
        transaction.setResponse(String.valueOf(TransactionResponse.ResponseEnum.APPROVED));
        transaction = transactionRepository.save(transaction);

        return toResponse(transaction);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAccountId(transaction.getAccount().getId());
        response.setCardId(transaction.getCard().getId());
        response.setTransactionAmount(transaction.getTransactionAmount());
        response.setTransactionType(transaction.getTransactionType());
        response.setTransactionDate(OffsetDateTime.ofInstant(transaction.getTransactionDate(), ZoneOffset.UTC));
        response.setResponse(TransactionResponse.ResponseEnum.valueOf(transaction.getResponse()));
        return response;
    }
}