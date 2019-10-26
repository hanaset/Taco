package com.hanaset.tacomccree.config;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PairConfig {

    private String exchange;

    private String asset;

    private String baseAsset;

    private BigDecimal volume;

    private BigDecimal fee;

    private BigDecimal askPrice; // 추가적으로 더 싸게 살지

    private BigDecimal bidPrice; // 추가적으로 더 비싸게 팔지
}
