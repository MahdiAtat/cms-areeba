package com.areeba.cms.cmsmircoservice.accounts.controller;

import com.areeba.cms.cmsmicroservice.controller.AccountsApi;
import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.accounts.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AccountsController implements AccountsApi {

    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<AccountResponse> createAccount(AccountCreateRequest account) {
        return ResponseEntity.ok(accountService.createAccountService(account));
    }

    @Override
    public ResponseEntity<Void> deleteAccountById(UUID id) {
        accountService.deleteAccountService(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountById(UUID id) {
        return ResponseEntity.ok(accountService.getAccountService(id));
    }

    @Override
    public ResponseEntity<AccountResponse> updateAccountById(UUID id, AccountCreateRequest account) {
        return ResponseEntity.ok(accountService.updateAccountService(id, account));
    }
}
