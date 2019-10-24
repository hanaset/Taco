package com.hanaset.tacocommon.api.probit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbitOrderRequest {

    @SerializedName("market_id")
    private String makretId;

    private String type;

    private String side;

    @SerializedName("time_in_force")
    private String timeInForce;

    @SerializedName("limit_price")
    private String limitPrice;

    private String cost;

    private String quantity;

    @SerializedName("client_order_id")
    private String clientOrderId;
}
