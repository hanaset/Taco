package com.hanaset.taco.utils;

import com.hanaset.taco.cache.OrderbookCached;

public class Taco2CurrencyConvert {

    public static Double convertBidBTC2KRW(Double btc) {

        return OrderbookCached.UPBIT_BTC.get("bid").doubleValue() * btc;
    }

    public static Double convertAskBTC2KRW(Double btc) {

        return OrderbookCached.UPBIT_BTC.get("ask").doubleValue() * btc;
    }

    public static Double convertPercent(Double curreny, Double percent) {

        return curreny * percent / 100.f;
    }
}
