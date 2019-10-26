package com.hanaset.tacocommon.entity.mccree;

import com.hanaset.tacocommon.common.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Data
@Builder
@Entity
@Table(name = "TB_MCCREE_ASSET")
public class McCreeAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exchange;

    private String asset;

    @Column(name = "base_asset")
    private String baseAsset;

    private BigDecimal volume;

    @Column(name = "ask_price")
    private BigDecimal askPrice; // 추가적으로 더 싸게 살지

    @Column(name = "bid_price")
    private BigDecimal bidPrice; // 추가적으로 더 비싸게 팔지

    private BigDecimal fee;

    private Boolean enable;

    private Integer interval;

    @Column(name = "reg_dtime", updatable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime regDateTime;

    @Column(name = "upd_dtime")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updDateTime;
}
