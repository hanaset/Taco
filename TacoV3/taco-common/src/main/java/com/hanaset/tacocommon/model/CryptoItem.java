package com.hanaset.tacocommon.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoItem {

    private String crypto;

    private BigDecimal amount;

    private Integer count;
}
