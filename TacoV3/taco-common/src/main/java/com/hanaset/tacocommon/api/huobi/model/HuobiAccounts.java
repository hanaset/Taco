package com.hanaset.tacocommon.api.huobi.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HuobiAccounts {

    private Long id;

    private String type;

    private String state;

    @SerializedName("user-id")
    private Long subType;
}
