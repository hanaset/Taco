package com.hanaset.taco.repository;

import com.hanaset.taco.entity.TransactionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLogEntity, Integer> {
}
