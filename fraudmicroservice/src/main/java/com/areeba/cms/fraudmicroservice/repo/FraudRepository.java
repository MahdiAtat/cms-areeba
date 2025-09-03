package com.areeba.cms.fraudmicroservice.repo;

import com.areeba.cms.cmsmircoservice.type.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
