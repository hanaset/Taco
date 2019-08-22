package com.hanaset.taco.cache;

import com.google.common.collect.Maps;
import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class OrderbookCached {

    public static BigDecimal UPBIT_BTC;

    public static Map<String, UpbitOrderbookItem> UPBIT = Maps.newHashMap();

}
