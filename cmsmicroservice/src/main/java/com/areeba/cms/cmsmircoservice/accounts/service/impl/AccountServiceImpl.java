package com.areeba.cms.cmsmircoservice.accounts.service.impl;

import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.accounts.repo.AccountRepository;
import com.areeba.cms.cmsmircoservice.accounts.service.AccountService;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.type.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer for basic account operations.
 * <p>Creates, reads, updates, and deletes accounts.
 * Write methods run in a transaction; field changes rely on JPA dirty checking.</p>
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Creates a new account from the request.
     *
     * @param req status and opening balance
     * @return the created account (includes generated id)
     */
    @Transactional
    @Override
    public AccountResponse createAccountService(AccountCreateRequest req) {
        Account account = new Account();
        account.setStatus(req.getStatus());
        account.setBalance(req.getBalance());
        account = accountRepository.save(account);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.status(account.getStatus());
        accountResponse.balance(account.getBalance());
        accountResponse.id(account.getId());
        return accountResponse;
    }

    /**
     * Loads the account entity or fails.
     *
     * @param id account id
     * @return the managed entity
     * @throws ResourceNotFoundException if the account does not exist
     */
    @Transactional(readOnly = true)
    @Override
    public Account requireAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("account not found id={}", id);
                    return new ResourceNotFoundException("Account not found");
                });
    }

    /**
     * Returns account details as a DTO.
     *
     * @param id account id
     * @return account view
     * @throws ResourceNotFoundException if the account does not exist
     */
    @Transactional(readOnly = true)
    @Override
    public AccountResponse getAccountService(UUID id) {
        Account account = requireAccount(id);

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.status(account.getStatus());
        accountResponse.balance(account.getBalance());
        accountResponse.id(account.getId());
        return accountResponse;
    }

    /**
     * Updates status and/or balance of an existing account.
     * <p>No explicit save call needed; changes flush on commit.</p>
     *
     * @param id  account id
     * @param req new values
     * @return updated account view
     * @throws ResourceNotFoundException if the account does not exist
     */
    @Transactional
    @Override
    public AccountResponse updateAccountService(UUID id, AccountCreateRequest req) {
        Account account = requireAccount(id);
        account.setStatus(req.getStatus());
        account.setBalance(req.getBalance());

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.status(account.getStatus());
        accountResponse.balance(account.getBalance());
        accountResponse.id(account.getId());
        return accountResponse;
    }

    /**
     * Deletes the account by id.
     *
     * @param id account id
     * @throws ResourceNotFoundException if the account does not exist
     */
    @Transactional
    @Override
    public void deleteAccountService(UUID id) {
        requireAccount(id);
        accountRepository.deleteById(id);
    }
}
