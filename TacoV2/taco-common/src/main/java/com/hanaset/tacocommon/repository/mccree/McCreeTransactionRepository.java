package com.hanaset.tacocommon.repository.mccree;

import com.hanaset.tacocommon.entity.mccree.McCreeTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface McCreeTransactionRepository extends JpaRepository<McCreeTransactionEntity, String> {
}
