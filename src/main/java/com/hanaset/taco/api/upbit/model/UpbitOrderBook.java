package com.hanaset.taco.api.upbit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpbitOrderBook {

    private String market;

    private Long timestamp;

    @SerializedName("total_ask_size")
    private Double totalAskSize;

    @SerializedName("total_bid_size")
    private Double totalBidSize;

    @SerializedName("orderbook_units")
    private List<UpbitOrderbookItem> orderbookUnits;
}
