package com.hanaset.taco.api.upbit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UpbitOrderbookItem {

    @SerializedName("ask_price")
    private Double askPrice;
    @SerializedName("bid_price")
    private Double bidPrice;
    @SerializedName("ask_size")
    private Double askSize;
    @SerializedName("bid_size")
    private Double bidSize;
}
