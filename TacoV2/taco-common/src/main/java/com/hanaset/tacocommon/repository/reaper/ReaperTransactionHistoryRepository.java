package com.hanaset.tacocommon.repository.reaper;

import com.hanaset.tacocommon.entity.reaper.ReaperTransactionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaperTransactionHistoryRepository extends JpaRepository<ReaperTransactionHistoryEntity, Long> {
}
