package com.hanaset.tacocommon.repository.mld;

import com.hanaset.tacocommon.entity.mld.MldAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MldAssetRepository extends JpaRepository<Long, MldAssetEntity> {
}
