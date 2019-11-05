package com.hanaset.tacocommon.entity.mccree;

import com.hanaset.tacocommon.common.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@Table(name = "TB_MCCREE_TRANSACTION")
public class McCreeTransactionEntity {

    @Id
    private String uuid;

    private String sid;

    @Column(name = "ord_type")
    private String ordType;

    private BigDecimal price;

    @Column(name = "avg_price")
    private BigDecimal avgPrice;

    private String state;

    private String market;

    @Column(name = "create_at")
    private String createAt;

    private BigDecimal volume;

    @Column(name = "remaining_volume")
    private BigDecimal remainingVolume;

    @Column(name = "reserved_fee")
    private BigDecimal reservedFee;

    @Column(name = "remaining_fee")
    private BigDecimal remainingFee;

    @Column(name = "paid_fee")
    private BigDecimal paidFee;

    private BigDecimal locked;

    @Column(name = "executed_volume")
    private BigDecimal executedVolume;

    @Column(name = "reg_dtime", updatable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime regDateTime;

    @Column(name = "upd_dtime")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updDateTime;
}
