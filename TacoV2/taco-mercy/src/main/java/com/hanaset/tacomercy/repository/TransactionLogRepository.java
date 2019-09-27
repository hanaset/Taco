package com.hanaset.tacomercy.repository;

import com.hanaset.tacomercy.entity.TransactionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLogEntity, Integer> {
}
