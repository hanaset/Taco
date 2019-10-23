package com.hanaset.tacocommon.entity.mld;

import com.hanaset.tacocommon.common.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@Table(name = "TB_MLD_ASSET")
public class MldAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset")
    private String asset;

    @Column(name = "reg_dtime", updatable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime regDateTime;

    @Column(name = "upd_dtime")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updDateTime;

}
