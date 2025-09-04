package com.areeba.cms.cmsmircoservice.accounts.service;

import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.type.Account;

import java.util.UUID;

/**
 * Account operations: create, read, update, delete, and a helper to load the entity.
 */
public interface AccountService {

    /**
     * Creates a new account.
     *
     * @param req request with status and opening balance
     * @return created account (includes generated id)
     */
    AccountResponse createAccountService(AccountCreateRequest req);

    /**
     * Returns a single account by id.
     *
     * @param id account id
     * @return account view
     * @throws ResourceNotFoundException if not found
     */
    AccountResponse getAccountService(UUID id);

    /**
     * Updates status/balance of an existing account.
     *
     * @param id  account id
     * @param req new values
     * @return updated account view
     * @throws ResourceNotFoundException if not found
     */
    AccountResponse updateAccountService(UUID id, AccountCreateRequest req);

    /**
     * Deletes the account by id.
     *
     * @param id account id
     * @throws ResourceNotFoundException if not found
     */
    void deleteAccountService(UUID id);

    /**
     * Loads the account entity or fails.
     * Useful when logic needs the managed JPA entity.
     *
     * @param id account id
     * @return the managed entity
     * @throws ResourceNotFoundException if not found
     */
    Account requireAccount(UUID id);

}
