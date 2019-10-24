package com.hanaset.tacocommon.api.probit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbitBalance {

    @SerializedName("currency_id")
    private String currencyId;

    private String total;

    private String available;
}
