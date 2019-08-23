package com.hanaset.taco.repository;

import com.hanaset.taco.entity.TransactionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistoryEntity, Long> {
}
