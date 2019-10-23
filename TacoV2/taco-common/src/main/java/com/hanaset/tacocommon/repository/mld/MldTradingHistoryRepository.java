package com.hanaset.tacocommon.repository.mld;

import com.hanaset.tacocommon.entity.mld.MldTradingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MldTradingHistoryRepository extends JpaRepository<Long, MldTradingHistoryEntity> {
}
