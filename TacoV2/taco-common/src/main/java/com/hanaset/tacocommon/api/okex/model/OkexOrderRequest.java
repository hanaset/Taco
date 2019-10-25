package com.hanaset.tacocommon.api.okex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OkexOrderRequest {

    private String type;

    private String side;

    @JsonProperty("instrument_id")
    private String instrumentId;

    @JsonProperty("order_type")
    private String orderType;

    private String price;

    private String fund;

    private String size;

    @JsonProperty("client_oid")
    private String clientOid;
}
