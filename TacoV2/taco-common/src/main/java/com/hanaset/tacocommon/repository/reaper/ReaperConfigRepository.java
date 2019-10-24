package com.hanaset.tacocommon.repository.reaper;

import com.hanaset.tacocommon.entity.reaper.ReaperConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaperConfigRepository extends JpaRepository<ReaperConfigEntity, Long> {
}
