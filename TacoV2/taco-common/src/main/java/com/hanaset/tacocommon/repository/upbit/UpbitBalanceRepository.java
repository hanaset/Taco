package com.hanaset.tacocommon.repository.upbit;

import com.hanaset.tacocommon.entity.upbit.UpbitBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpbitBalanceRepository extends JpaRepository<UpbitBalanceEntity, Long> {
}
