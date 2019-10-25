package com.hanaset.tacocommon.api.okex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OkexOrderResponse {

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("error_code")
    private String errorCode;

    @SerializedName("error_message")
    private String errorMessage;

    @SerializedName("client_oid")
    private String clientOid;

    private Boolean result;
}
