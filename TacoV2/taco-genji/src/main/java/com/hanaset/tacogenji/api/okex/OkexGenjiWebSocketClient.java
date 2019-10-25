package com.hanaset.tacogenji.api.okex;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hanaset.tacocommon.api.okex.model.body.OkexWebSocketOp;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketTicket;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketType;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import com.hanaset.tacogenji.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class OkexGenjiWebSocketClient {

    private final TradeUrlProperties tradeUrlProperties;
    private final TransactionService transactionService;

    private WebSocketSession webSocketSession;

    public OkexGenjiWebSocketClient(TradeUrlProperties tradeUrlProperties,
                                     TransactionService transactionService) {
        this.tradeUrlProperties = tradeUrlProperties;
        this.transactionService = transactionService;
    }

    public void connect(OkexWebSocketOp okexWebSocketOp) {

        log.info("Connecting to Okex Web Socket Server...");

        String body = new Gson().toJson(okexWebSocketOp);

        log.info("Okex send message: {}", body);

        WebSocketClient webSocketClient;

        try {
            webSocketClient = new StandardWebSocketClient();
            webSocketSession =
                    webSocketClient.doHandshake(new OkexGenjiWebSocketHandler(), new WebSocketHttpHeaders(), URI.create(tradeUrlProperties.getOkexWebSockUrl())).get();

            try {
                TextMessage message = new TextMessage(body);
                webSocketSession.sendMessage(message);
                log.info("[{}] Okex Client successfully sent ticker subscription message", tradeUrlProperties.getOkexWebSockUrl());
            } catch (Exception e) {
                log.error("Exception while sending a message", e);
            }

        } catch (Exception e) {
            log.error("Exception while accessing websockets", e);
        }
    }

    public void disconnect() {

        try {
            webSocketSession.close();
            log.info("<======================== WebSocket Closed =======================>");
        } catch (IOException e) {
            log.error("WebSocket Close Error");
        } catch (NullPointerException e) {
            log.error("Not Connect WebSocket");
        }

    }

}
