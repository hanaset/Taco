package com.hanaset.tacocommon.api.upbit.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpbitTrade {

    private String code;

    private BigDecimal trade_volume;

    private String trade_time;

    private BigDecimal trade_price;

    private BigDecimal sequential_id;

    private Long trade_timestamp;

    private String change;

    private String type;

    private String trade_date;

    private String stream_type;

    private BigDecimal prev_closing_price;

    private BigDecimal change_price;

    private String ask_bid;

    private Long timestamp;
}
