package com.hanaset.taco.api.upbit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class UpbitOrderBook {

    @SerializedName("type")
    private String type;

    @SerializedName("code")
    private String code;

    @SerializedName("timestamp")
    private Long timestamp;

    @SerializedName(value = "total_ask_size")
    private Double total_ask_size;

    @SerializedName("total_bid_size")
    private Double total_bid_size;

    @SerializedName("orderbook_units")
    private List<UpbitOrderbookItem> orderbook_units;

    private String stream_type;
}
