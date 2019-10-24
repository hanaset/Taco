package com.hanaset.tacocommon.api.probit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbitOrderCancelRequest {

    @SerializedName("market_id")
    private String marketId;

    @SerializedName("order_id")
    private String orderId;
}
