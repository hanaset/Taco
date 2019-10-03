package com.hanaset.tacocommon.repository;

import com.hanaset.tacocommon.entity.PairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PairRepository extends JpaRepository<PairEntity, Integer> {

    @Query(value = "select t.crypto from tb_pair t where snapshot between ?1 and ?2 group by t.crypto order by sum(t.profit_amount) desc ", nativeQuery = true)
    List<String> getCryptoOfSumAmountAndCount(String start, String end);

    @Query(value = "select t.crypto from tb_pair t where snapshot=?1 group by t.crypto order by sum(t.profit_amount) desc ", nativeQuery = true)
    List<String> getCurrentCryptoOfSumAmountAndCount(String date);
}
