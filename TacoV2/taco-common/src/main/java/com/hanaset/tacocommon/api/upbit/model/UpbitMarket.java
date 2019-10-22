package com.hanaset.tacocommon.api.upbit.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpbitMarket {

    private String market;

    private String korean_name;

    private String english_name;
}
