package com.areeba.cms.cmsmircoservice.cards.repo;

import com.areeba.cms.cmsmicroservice.type.AccountResponse;
import com.areeba.cms.cmsmircoservice.type.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;


public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<AccountResponse> findByIdForUpdate(UUID id);
}
