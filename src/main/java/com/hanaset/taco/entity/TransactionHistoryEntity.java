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
    private Double askAmount;

    @Column(name = "bid_amount")
    private Double bidAmount;

    @Column(name = "ask_price")
    private Double askPrice;

    @Column(name = "bid_price")
    private Double bidPrice;

    @Column(name = "ask_snapshot")
    private ZonedDateTime askSnapShot;

    @Column(name = "bid_snapshot")
    private ZonedDateTime bidSnapShot;

    @Column(name = "now_btc")
    private Double nowBTC;

    private Double profit;

    private Double fee;
}
