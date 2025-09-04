package com.areeba.cms.cmsmircoservice.accounts.controller;

import com.areeba.cms.cmsmicroservice.controller.AccountsApi;
import com.areeba.cms.cmsmicroservice.type.AccountCardIdsResponse;
import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.accounts.service.AccountService;
import com.areeba.cms.cmsmircoservice.cards.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AccountsController implements AccountsApi {

    private static final Logger log = LoggerFactory.getLogger(AccountsController.class);

    private final AccountService accountService;
    private final CardService cardService;

    public AccountsController(AccountService accountService, CardService cardService) {
        this.accountService = accountService;
        this.cardService = cardService;
    }

    @Override
    public ResponseEntity<AccountResponse> createAccount(AccountCreateRequest account) {
        log.debug("Creating account {}", account);
        AccountResponse accountResponse = accountService.createAccountService(account);
        log.info("Account created id={}", accountResponse.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponse);
    }

    @Override
    public ResponseEntity<Void> deleteAccountById(UUID id) {
        log.debug("Deleting account {}", id);
        accountService.deleteAccountService(id);
        log.info("Account deleted {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountById(UUID id) {
        log.debug("Fetching account {}", id);
        AccountResponse accountResponse = accountService.getAccountService(id);
        log.info("Account fetched {}", id);
        return ResponseEntity.ok(accountResponse);
    }

    @Override
    public ResponseEntity<AccountCardIdsResponse> listCardIdsForAccount(UUID id, Integer page, Integer size) {
        log.debug("Fetching card ids for account {}", id);
        AccountCardIdsResponse accountCardIdsResponse = cardService.listCardIdsByAccount(id, page, size);
        log.info("Card ids fetched {}", id);
        return ResponseEntity.ok(accountCardIdsResponse);
    }

    @Override
    public ResponseEntity<AccountResponse> updateAccountById(UUID id, AccountCreateRequest account) {
        log.debug("Updating account {}", id);
        AccountResponse accountResponse = accountService.updateAccountService(id, account);
        log.info("Updated account {}", id);
        return ResponseEntity.ok(accountResponse);
    }
}
