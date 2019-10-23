package com.hanaset.tacocommon.repository.mld;

import com.hanaset.tacocommon.entity.mld.MldBalanceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MldBalanceHistoryRepository extends JpaRepository<Long, MldBalanceHistoryEntity> {
}
