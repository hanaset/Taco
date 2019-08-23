package com.hanaset.taco.repository;

import com.hanaset.taco.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, Long> {
}
