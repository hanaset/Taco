package com.hanaset.tacocommon.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Builder
@Data
@Entity
public class TransactionLogEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "crypto")
    private String crypto;

    @Column(name = "profit_amount")
    private BigDecimal profitAmount;

    @Column(name = "profit_percent")
    private BigDecimal profitPercent;

    @Column(name = "snapshot")
    private ZonedDateTime snapshot;

}
