package com.hanaset.taco.api.upbit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpbitAccount {

    @SerializedName("currency")
    private String currency;

    @SerializedName("balance")
    private BigDecimal balance;

    @SerializedName("locked")
    private BigDecimal locked;

    @SerializedName("avg_buy_price")
    private BigDecimal avg_buy_price;

    @SerializedName("avg_buy_price_modified")
    private Boolean avg_buy_price_modified;

    @SerializedName("unit_currency")
    private String unit_currency;
}
