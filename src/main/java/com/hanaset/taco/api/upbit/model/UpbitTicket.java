package com.hanaset.taco.api.upbit.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpbitTicket {

    private String uuid;

    private String market;

    private String ask_market;

    private String bid_market;

    private BigDecimal amount;

    private UpbitOrderbookItem askOrderbookItem;

    private UpbitOrderbookItem bidOrderbookItem;

    private BigDecimal real_amount;
}
