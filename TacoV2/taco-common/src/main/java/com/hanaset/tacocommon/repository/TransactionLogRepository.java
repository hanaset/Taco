package com.hanaset.tacocommon.repository;

import com.hanaset.tacocommon.entity.TransactionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLogEntity, Integer> {
}
