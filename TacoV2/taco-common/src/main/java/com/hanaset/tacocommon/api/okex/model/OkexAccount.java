package com.hanaset.tacocommon.api.okex.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OkexAccount {

    private BigDecimal hold;

    private String currency;

    private BigDecimal balance;

    private BigDecimal available;
}
