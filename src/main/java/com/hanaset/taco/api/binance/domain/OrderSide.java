package com.hanaset.taco.api.binance.domain;

/**
 * Buy/Sell order side.
 */
public enum OrderSide {
    BUY("BUY"),
    SELL("SELL");

    private final String code;

    OrderSide(String code) {

        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
