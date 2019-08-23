package com.hanaset.taco.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@Table(name = "TB_TRANSACTION_HISTORY")
public class TransactionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String market;

    @Column(name = "ask_pair")
    private String askPair;

    @Column(name = "bid_pair")
    private String bidPair;

    @Column(name = "ask_amount")
    private BigDecimal askAmount;

    @Column(name = "bid_amount")
    private BigDecimal bidAmount;

    @Column(name = "ask_price")
    private BigDecimal askPrice;

    @Column(name = "bid_price")
    private BigDecimal bidPrice;

    @Column(name = "ask_snapshot")
    private ZonedDateTime askSnapShot;

    @Column(name = "bid_snapshot")
    private ZonedDateTime bidSnapShot;

    private BigDecimal nowBTC;

    private BigDecimal profit;

    private BigDecimal fee;
}
