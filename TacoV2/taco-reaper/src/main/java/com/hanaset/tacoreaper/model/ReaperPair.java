package com.hanaset.tacoreaper.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReaperPair {

    private String side;

    private BigDecimal price;
}
