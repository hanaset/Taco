package com.hanaset.tacoreaper.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReaperTradeCondition {

    private String pair;

    private String baseAsset;

    private BigDecimal orderMinVolume;

    private BigDecimal orderMaxVolume;

    private Integer interval;

    private BigDecimal tradingVolume;

    private Integer precision;

    private BigDecimal unit;
}
