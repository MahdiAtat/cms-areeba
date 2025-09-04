package com.areeba.cms.cmsmircoservice.transactions.repo;

import com.areeba.cms.cmsmircoservice.type.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for {@link Transaction}.
 * <p>
 * Provides basic CRUD via {@link JpaRepository}. A new transaction row is written
 * for each request (approved or rejected), and later read for reporting/audit.
 * </p>
 */
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
