package com.hanaset.tacocommon.entity.upbit;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@Data
@Entity
@Table(name = "tb_pair")
public class UpbitPairEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "crypto")
    private String crypto;

    @Column(name = "ask_price")
    private BigDecimal askPirce;

    @Column(name = "bid_price")
    private BigDecimal bidPrice;

    @Column(name = "ask_amount")
    private BigDecimal askAmount;

    @Column(name = "bid_amount")
    private BigDecimal bidAmount;

    @Column(name = "profit_amount")
    private BigDecimal profitAmount;

    @Column(name = "profit_percent")
    private BigDecimal profitPercent;

    @Column(name = "snapshot")
    private String snapshot;

    @Column(name = "reg_dtime")
    private Timestamp regDtime;
}
