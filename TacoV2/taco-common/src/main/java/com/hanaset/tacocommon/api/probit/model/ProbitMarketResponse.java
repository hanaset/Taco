package com.hanaset.tacocommon.api.probit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProbitMarketResponse {

    private String id;

    @SerializedName("base_currency_id")
    private String baseCurrencyId;

    @SerializedName("quote_currency_id")
    private String qouteCurrencyId;

    @SerializedName("min_price")
    private BigDecimal minPrice;

    @SerializedName("max_price")
    private BigDecimal maxPrice;

    @SerializedName("price_increment")
    private BigDecimal priceIncrement;

    @SerializedName("min_quantity")
    private BigDecimal minQuantity;

    @SerializedName("max_quantity")
    private BigDecimal maxQuantity;

    @SerializedName("quantity_precision")
    private Integer quantityPrecision;

    @SerializedName("min_cost")
    private BigDecimal minCost;

    @SerializedName("max_cost")
    private BigDecimal maxCost;

    @SerializedName("cost_precision")
    private Integer costPrecision;

    @SerializedName("taker_fee_rate")
    private String takerFeeRate;

    @SerializedName("maker_fee_rate")
    private String makerFeeRate;

    @SerializedName("show_in_ui")
    private Boolean showInUi;

    private Boolean closed;
}
