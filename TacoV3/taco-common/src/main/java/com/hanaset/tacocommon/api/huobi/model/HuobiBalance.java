package com.hanaset.tacocommon.api.huobi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HuobiBalance {

    private String currency;

    private String type;

    private String balance;
}
