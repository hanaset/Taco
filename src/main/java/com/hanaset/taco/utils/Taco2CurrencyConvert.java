package com.hanaset.taco.utils;

import com.hanaset.taco.cache.OrderbookCached;

public class Taco2CurrencyConvert {

    public static Double convertBTC2KRW(Double btc) {

        return OrderbookCached.UPBIT_BTC.doubleValue() * btc;
    }

    public static Double convertPercent(Double curreny, Double percent) {

        return curreny * percent / 100.f;
    }
}
