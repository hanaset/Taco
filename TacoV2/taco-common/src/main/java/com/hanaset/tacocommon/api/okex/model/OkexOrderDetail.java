package com.hanaset.tacocommon.api.okex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OkexOrderDetail {

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("client_oid")
    private String clientOid;

    private BigDecimal price;

    private BigDecimal size;

    private String notional;

    @SerializedName("instrument_id")
    private String instrumentId;

    private String side;

    private String type;

    private String timestamp;

    @SerializedName("filled_size")
    private BigDecimal filledSize;

    @SerializedName("filled_notional")
    private BigDecimal filledNotional;

    private String status;

    private String state;

    @SerializedName("order_type")
    private String orderType;

    @SerializedName("price_avg")
    private BigDecimal priceAvg;
}
