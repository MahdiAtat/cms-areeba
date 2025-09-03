package com.areeba.cms.cmsmircoservice.accounts.service;

import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.type.Account;

import java.util.UUID;

public interface AccountService {

    AccountResponse createAccountService(AccountCreateRequest req);

    AccountResponse getAccountService(UUID id);

    AccountResponse updateAccountService(UUID id, AccountCreateRequest req);

    void deleteAccountService(UUID id);

    Account requireAccount(UUID id);

}
