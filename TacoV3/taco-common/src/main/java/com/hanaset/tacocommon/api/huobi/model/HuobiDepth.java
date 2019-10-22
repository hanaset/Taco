package com.hanaset.tacocommon.api.huobi.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HuobiDepth {

    private String status;

    private Long ts;

    private String ch;

    @SerializedName("err-code")
    private String errCode;

    @SerializedName("err-msg")
    private String errMsg;

    private HuobiDepthTick tick;
}
