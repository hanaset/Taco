package com.hanaset.taco.api.binance.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaset.taco.api.binance.BinanceApiCallback;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

@Slf4j
public class BinanceApiWebSocketListener<T> extends WebSocketListener {

    private BinanceApiCallback<T> callback;

    private Class<T> eventClass;

    public BinanceApiWebSocketListener(BinanceApiCallback<T> callback, Class<T> eventClass) {
        this.callback = callback;
        this.eventClass = eventClass;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);

        callback.onOpen(webSocket, response);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {

        System.out.println("onMessage - string ==>" + text);

        ObjectMapper mapper = new ObjectMapper();
        try {
            T event = mapper.readValue(text, eventClass);
            callback.onResponse(event);
        } catch (Exception e) {

            log.error("binance websocket parse error : " + e.getMessage());

            e.printStackTrace();

//            throw new BinanceApiException(e);
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        super.onMessage(webSocket, bytes);

        System.out.println("onMessage - bytestring ==> " + bytes);

        ObjectMapper mapper = new ObjectMapper();
        try {
            T event = mapper.readValue(bytes.string(Charset.defaultCharset()), eventClass);
            callback.onResponse(event);
        } catch (Exception e) {

            log.error("binance websocket parse error : " + e.getMessage());

            e.printStackTrace();

//            throw new BinanceApiException(e);
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);

        callback.onClosed();
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);

        callback.onClosing();
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        super.onFailure(webSocket, t, response);

        log.error("binance websocket failure : " + t.getMessage() + " response : " + response);

        callback.onFailure(webSocket, t, response);

//        throw new BinanceApiException(t);
    }
}