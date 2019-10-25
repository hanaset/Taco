package com.hanaset.tacocommon.entity.reaper;

import com.hanaset.tacocommon.common.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@Table(name = "TB_REAPER_ASSET")
public class ReaperAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asset;

    @Column(name = "base_asset")
    private String baseAsset;

    @Column(name = "order_min_volume")
    private BigDecimal orderMinVolume;

    @Column(name = "order_max_volume")
    private BigDecimal orderMaxVolume;

    private Integer interval;

    @Column(name = "tradeVolume")
    private BigDecimal tradingVolume;

    private Double fee;

    private Integer precision;

    private BigDecimal unit;

    private Boolean enable;

    @Column(name = "reg_dtime", updatable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime regDateTime;

    @Column(name = "upd_dtime")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updDateTime;

}
