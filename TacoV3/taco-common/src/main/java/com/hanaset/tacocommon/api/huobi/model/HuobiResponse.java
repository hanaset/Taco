package com.hanaset.tacocommon.api.huobi.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HuobiResponse <T> {

    private String status;

    @SerializedName("err-code")
    private String errCode;

    @SerializedName("err-msg")
    private String errMsg;

    private T data;
}
