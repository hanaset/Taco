package com.hanaset.taco.api.binance;

import com.hanaset.taco.api.binance.exception.BinanceApiException;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * BinanceApiCallback is a functional interface used together with the BinanceApiAsyncClient to provide a non-blocking REST client.
 *
 * @param <T> the return type from the callback
 */
public interface BinanceApiCallback<T> {

    void onOpen(WebSocket webSocket, Response response);

    /**
     * Called whenever a response comes back from the Binance API.
     *
     * @param response the expected response object
     * @throws BinanceApiException if it is not possible to obtain the expected response object (e.g. incorrect API-KEY).
     */
    void onResponse(T response) throws BinanceApiException;

    void onClosing();

    void onClosed();

    void onFailure(WebSocket webSocket, Throwable throwable, Response response);

    void terminate();
}