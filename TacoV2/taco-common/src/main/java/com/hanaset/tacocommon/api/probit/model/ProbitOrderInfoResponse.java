package com.hanaset.tacocommon.api.probit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbitOrderInfoResponse {

    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("market_id")
    private String marketId;

    private String type;

    private String side;

    private String quantity;

    @SerializedName("limit_price")
    private String limitPrice;

    @SerializedName("time_in_force")
    private String timeInForce;

    @SerializedName("filled_cost")
    private String filledCost;

    @SerializedName("filled_quantity")
    private String filledQuantity;

    @SerializedName("open_quantity")
    private String openQuantity;

    private String status;

    private String time;

    @SerializedName("client_order_id")
    private String clientOrderId;
}
