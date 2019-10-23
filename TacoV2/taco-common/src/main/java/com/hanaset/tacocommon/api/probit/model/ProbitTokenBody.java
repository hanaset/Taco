package com.hanaset.tacocommon.api.probit.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbitTokenBody {

    @SerializedName("grant_type")
    private String grantType;
}
