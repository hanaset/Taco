package com.hanaset.tacocommon.repository.mccree;

import com.hanaset.tacocommon.entity.mccree.McCreeAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface McCreeAssetRepository extends JpaRepository<McCreeAssetEntity, Long> {

    List<McCreeAssetEntity> findByEnable(Boolean enable);
}
