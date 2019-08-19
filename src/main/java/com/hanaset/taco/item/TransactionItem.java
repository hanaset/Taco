package com.hanaset.taco.item;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class TransactionItem {

    private String market;

    private String pair;

    private BigDecimal price;

    private BigDecimal amount;
}
