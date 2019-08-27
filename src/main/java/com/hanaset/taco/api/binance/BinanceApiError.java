package com.hanaset.taco.api.binance;

import lombok.Data;

/**
 * Binance API error object.
 */
@Data
public class BinanceApiError {

    /**
     * UpbitError code.
     */
    private int code;

    /**
     * UpbitError message.
     */
    private String msg;

}
