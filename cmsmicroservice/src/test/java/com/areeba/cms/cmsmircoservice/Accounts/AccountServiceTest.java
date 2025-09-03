package com.areeba.cms.cmsmircoservice.Accounts;

import com.areeba.cms.cmsmicroservice.type.AccountCreateRequest;
import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmicroservice.type.AccountStatus;
import com.areeba.cms.cmsmircoservice.accounts.repo.AccountRepository;
import com.areeba.cms.cmsmircoservice.accounts.service.impl.AccountServiceImpl;
import com.areeba.cms.cmsmircoservice.exception.ResourceNotFoundException;
import com.areeba.cms.cmsmircoservice.type.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccountService_OK() {

        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setStatus(AccountStatus.ACTIVE);
        accountCreateRequest.setBalance(new BigDecimal("1500.00"));

        UUID generatedId = UUID.randomUUID();

        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(generatedId);
            return a;
        });

        AccountResponse accountResponse = accountService.createAccountService(accountCreateRequest);

        assertEquals(generatedId, accountResponse.getId());
        assertEquals(AccountStatus.ACTIVE, accountResponse.getStatus());
        assertEquals(0, accountResponse.getBalance().compareTo(new BigDecimal("1500.00")));
        verify(accountRepository, times(1)).save(any(Account.class));

    }

    @Test
    void requireAccount_found() {
        UUID id = UUID.randomUUID();
        Account account = new Account();
        account.setId(id);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("100.00"));

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        Account got = accountService.requireAccount(id);
        assertSame(account, got);
        verify(accountRepository).findById(id);
    }

    @Test
    void requireAccount_notFound() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> accountService.requireAccount(id));
        assertEquals("Account not found", ex.getMessage());
        verify(accountRepository).findById(id);
    }

    @Test
    void getAccountService_OK() {
        UUID id = UUID.randomUUID();
        Account account = new Account();
        account.setId(id);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("250.00"));

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        AccountResponse accountResponse = accountService.getAccountService(id);
        assertEquals(id, accountResponse.getId());
        assertEquals(AccountStatus.ACTIVE, accountResponse.getStatus());
        assertEquals(0, accountResponse.getBalance().compareTo(new BigDecimal("250.00")));
        verify(accountRepository).findById(id);
    }

    @Test
    void updateAccountService_OK() {
        UUID id = UUID.randomUUID();
        Account existing = new Account();
        existing.setId(id);
        existing.setStatus(AccountStatus.INACTIVE);
        existing.setBalance(new BigDecimal("100.00"));

        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));

        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setStatus(AccountStatus.ACTIVE);
        accountCreateRequest.setBalance(new BigDecimal("999"));

        AccountResponse accountResponse = accountService.updateAccountService(id, accountCreateRequest);

        assertEquals(id, accountResponse.getId());
        assertEquals(AccountStatus.ACTIVE, accountResponse.getStatus());
        assertEquals(0, accountResponse.getBalance().compareTo(new BigDecimal("999")));


        assertEquals(AccountStatus.ACTIVE, existing.getStatus());
        assertEquals(0, existing.getBalance().compareTo(new BigDecimal("999")));


        verify(accountRepository, never()).save(any(Account.class));
        verify(accountRepository).findById(id);
    }

    @Test
    void deleteAccountService_OK() {
        UUID id = UUID.randomUUID();
        Account a = new Account();
        a.setId(id);

        when(accountRepository.findById(id)).thenReturn(Optional.of(a));

        accountService.deleteAccountService(id);

        verify(accountRepository).findById(id);
        verify(accountRepository).deleteById(id);
    }

    @Test
    void deleteAccountService_KO() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccountService(id));

        verify(accountRepository).findById(id);
        verify(accountRepository, never()).deleteById(any());
    }

}
