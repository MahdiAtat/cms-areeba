package com.areeba.cms.cmsmircoservice.transactions.service.impl;

import com.areeba.cms.cmsmicroservice.type.*;
import com.areeba.cms.cmsmircoservice.accounts.repo.AccountRepository;
import com.areeba.cms.cmsmircoservice.cards.repo.CardRepository;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.exception.TransactionRejectedException;
import com.areeba.cms.cmsmircoservice.rest.FraudClient;
import com.areeba.cms.cmsmircoservice.transactions.repo.TransactionRepository;
import com.areeba.cms.cmsmircoservice.transactions.service.TransactionService;
import com.areeba.cms.cmsmircoservice.type.Account;
import com.areeba.cms.cmsmircoservice.type.Card;
import com.areeba.cms.cmsmircoservice.type.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Handles transaction creation: validation, fraud check, balance update, and persistence.
 * <p>Runs in a single DB transaction; account row is fetched with a write lock to serialize balance changes.</p>
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final FraudClient fraudClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, CardRepository cardRepository, FraudClient fraudClient) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.fraudClient = fraudClient;
    }

    /**
     * Creates a debit/credit transaction.
     * <p>Flow:</p>
     * <ol>
     *   <li>Lock account row for update (pessimistic write).</li>
     *   <li>Validate card (ACTIVE, not expired, belongs to account).</li>
     *   <li>Validate account (ACTIVE, sufficient balance for debits).</li>
     *   <li>Call fraud service; if rejected, persist a rejected transaction and return.</li>
     *   <li>Apply balance change and persist an approved transaction.</li>
     * </ol>
     *
     * @param request amount, type (C/D), accountId, cardId
     * @return persisted transaction as a response DTO
     * @throws ResourceNotFoundException    if account or card is missing
     * @throws TransactionRejectedException on eligibility failures (inactive/expired/ownership/insufficient)
     */
    @Transactional
    @Override
    public TransactionResponse createTransactionService(TransactionCreateRequest request) {
        // Load and lock account for update
        log.debug("Locking account {}", request.getAccountId());
        Account account = accountRepository.findByIdForUpdate(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Card eligibility
        log.debug("Checking card eligibility {}", request.getAccountId());
        if (card.getStatus() != CardStatus.ACTIVE)
            throw new TransactionRejectedException("Card not active");
        if (card.getExpiry().isBefore(LocalDate.now()))
            throw new TransactionRejectedException("Card expired");
        if (!card.getAccount().getId().equals(account.getId()))
            throw new TransactionRejectedException("Card does not belong to account");

        // Account eligibility
        log.debug("Checking account eligibility {}", request.getAccountId());
        if (account.getStatus() != AccountStatus.ACTIVE)
            throw new TransactionRejectedException("Account not active");
        BigDecimal amount = request.getTransactionAmount();
        if (request.getTransactionType() == TransactionType.D && account.getBalance().compareTo(amount) < 0)
            throw new TransactionRejectedException("Insufficient balance");

        // Fraud check
        log.debug("Checking fraud {}", request.getAccountId());
        FraudCheckRequest fraudCheckRequest = new FraudCheckRequest();
        fraudCheckRequest.setAmount(amount);
        fraudCheckRequest.setCardId(card.getId());
        fraudCheckRequest.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        FraudCheckResponse fraud = fraudClient.evaluate(fraudCheckRequest);
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCard(card);
        transaction.setTransactionAmount(amount);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionDate(Instant.now());
        if (!fraud.getApproved()) {
            // Save rejected transaction record
            transaction.setResponse("REJECTED");
            transactionRepository.save(transaction);
            throw new TransactionRejectedException("Possible fraud detected");
        }

        // Apply balance
        log.debug("Apply balance {}", request.getAccountId());
        if (request.getTransactionType() == TransactionType.D) {
            account.setBalance(account.getBalance().subtract(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
        }

        // Save transaction
        transaction.setResponse(String.valueOf(TransactionResponse.ResponseEnum.APPROVED));
        log.debug("Saving transaction for account {}", transaction.getAccount().getId());
        transaction = transactionRepository.save(transaction);
        log.info("Transaction saved {}", transaction.getId());

        return toResponse(transaction);
    }

    /**
     * Maps a {@link Transaction} entity to its API response.
     * <p>Date is returned as {@code OffsetDateTime} in UTC.</p>
     *
     * @param transaction managed entity
     * @return response DTO
     */
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