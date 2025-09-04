package com.areeba.cms.cmsmircoservice.accounts.repo;

import com.areeba.cms.cmsmircoservice.type.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Account}.
 * <p>Includes a helper to fetch an account with a DB row lock
 * when it's about to change the balance.</p>
 */
public interface AccountRepository extends JpaRepository<Account, UUID> {

    /**
     * Fetches an account by id and acquires a <b>pessimistic write</b> lock
     * <p>Call inside a non-read-only {@code @Transactional} method.</p>
     *
     * @param id account id
     * @return the account if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") UUID id);
}
