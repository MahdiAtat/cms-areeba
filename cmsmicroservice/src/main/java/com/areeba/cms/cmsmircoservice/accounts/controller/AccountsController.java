package com.areeba.cms.cmsmircoservice.accounts.controller;

import com.areeba.cms.cmsmicroservice.controller.AccountsApi;
import com.areeba.cms.cmsmicroservice.type.AccountCardIdsResponse;
import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.accounts.service.AccountService;
import com.areeba.cms.cmsmircoservice.cards.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AccountsController implements AccountsApi {

    private final AccountService accountService;
    private final CardService cardService;

    public AccountsController(AccountService accountService, CardService cardService) {
        this.accountService = accountService;
        this.cardService = cardService;
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
    public ResponseEntity<AccountCardIdsResponse> listCardIdsForAccount(UUID id, Integer page, Integer size) {
        return ResponseEntity.ok(cardService.listCardIdsByAccount(id, page, size));
    }

    @Override
    public ResponseEntity<AccountResponse> updateAccountById(UUID id, AccountCreateRequest account) {
        return ResponseEntity.ok(accountService.updateAccountService(id, account));
    }
}
