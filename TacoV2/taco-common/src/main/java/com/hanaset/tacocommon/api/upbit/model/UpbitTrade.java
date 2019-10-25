package com.hanaset.tacocommon.api.upbit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpbitTrade {

    private String code;

    @JsonProperty("trade_volume")
    private BigDecimal tradeVolume;

    @JsonProperty("trade_time")
    private String tradeTime;

    @JsonProperty("trade_price")
    private BigDecimal tradePrice;

    @JsonProperty("sequential_id")
    private BigDecimal sequentialId;

    @JsonProperty("trade_timestamp")
    private Long tradeTimestamp;

    private String change;

    private String type;

    @JsonProperty("trade_date")
    private String tradeDate;

    @JsonProperty("stream_type")
    private String streamType;

    @JsonProperty("prev_closing_price")
    private BigDecimal prevClosingPrice;

    @JsonProperty("change_price")
    private BigDecimal changePrice;

    @JsonProperty("ask_bid")
    private String askBid;

    private Long timestamp;
}
