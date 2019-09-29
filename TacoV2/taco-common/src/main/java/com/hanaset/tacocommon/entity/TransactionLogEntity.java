package com.hanaset.tacocommon.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Data
@Entity
@Table(name = "TRANSACTION_LOG")
public class TransactionLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "crypto")
    private String crypto;

    @Column(name = "profit_amount")
    private BigDecimal profitAmount;

    @Column(name = "profit_percent")
    private BigDecimal profitPercent;

    @Column(name = "snapshot")
    private String snapshot;
}
