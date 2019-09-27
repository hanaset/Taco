package com.hanaset.tacocommon.api.upbit.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpbitOrderRequest {

    private String market;

    private String side;

    private String volume;

    private String price;

    private String ord_type;
}
