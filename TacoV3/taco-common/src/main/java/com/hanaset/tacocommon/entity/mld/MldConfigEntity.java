package com.hanaset.tacocommon.entity.mld;

import com.hanaset.tacocommon.common.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@Table(name = "TB_MLD_CONFIG")
public class MldConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "base_asset")
    private String baseAsset;

    @Column(name = "base_asset_balance")
    private BigDecimal baseAssetBalance;

    @Column(name = "min_trading_volume")
    private Integer minTradingVolume;

    @Column(name = "max_order_amount")
    private Integer maxOrderAmount;

    @Column(name = "interval")
    private Integer interval;

    @Column(name = "bid_fee_rate")
    private BigDecimal bidFeeRate;

    @Column(name = "ask_fee_rate")
    private BigDecimal askFeeRate;

    @Column(name = "reg_dtime", updatable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime regDateTime;

    @Column(name = "upd_dtime")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updDateTime;

}
