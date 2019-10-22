package com.hanaset.tacocommon.api.okex.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OkexBalance {

    private String currency;

    private BigDecimal balance;

    private String hold;

    private String available;
}
