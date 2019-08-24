package com.hanaset.taco.api.upbit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpbitOrderbookItem {

    @SerializedName("ask_price")
    private Double ask_price;
    @SerializedName("bid_price")
    private Double bid_price;
    @SerializedName("ask_size")
    private Double ask_size;
    @SerializedName("bid_size")
    private Double bid_size;
}
