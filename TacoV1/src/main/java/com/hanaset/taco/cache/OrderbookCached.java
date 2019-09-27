package com.hanaset.taco.cache;

import com.google.common.collect.Maps;
import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class OrderbookCached {

    public static Map<String, BigDecimal> UPBIT_BTC = Maps.newHashMap();

    public static Map<String, UpbitOrderbookItem> UPBIT = Maps.newHashMap();

    public static boolean UPBIT_LOCK = false;

    public static Map<String, Boolean> UPBIT_LOCKS = Maps.newHashMap();

    public static synchronized void lock_chage(Boolean lock){
        UPBIT_LOCK = lock;
    }

}
