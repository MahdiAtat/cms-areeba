package com.areeba.cms.cmsmircoservice.accounts.service.impl;

import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.accounts.repo.AccountRepository;
import com.areeba.cms.cmsmircoservice.accounts.service.AccountService;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.type.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;


    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

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

    @Transactional(readOnly = true)
    public Account requireAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

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

    @Transactional
    @Override
    public void deleteAccountService(UUID id) {
        requireAccount(id);
        accountRepository.deleteById(id);
    }
}
