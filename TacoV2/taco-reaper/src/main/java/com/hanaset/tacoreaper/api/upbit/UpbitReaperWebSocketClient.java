package com.hanaset.tacoreaper.api.upbit;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketTicket;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketType;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import com.hanaset.tacoreaper.service.okex.ReaperOkexTradeService;
import com.hanaset.tacoreaper.service.probit.ReaperProbitTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
public class UpbitReaperWebSocketClient {

    private final TradeUrlProperties tradeUrlProperties;
    private final ReaperProbitTradeService reaperProbitTradeService;
    private final ReaperOkexTradeService reaperOkexTradeService;
    private WebSocketSession webSocketSession;

    public UpbitReaperWebSocketClient(TradeUrlProperties tradeUrlProperties,
                                      ReaperProbitTradeService reaperProbitTradeService,
                                      ReaperOkexTradeService reaperOkexTradeService) {
        this.tradeUrlProperties = tradeUrlProperties;
        this.reaperOkexTradeService = reaperOkexTradeService;
        this.reaperProbitTradeService = reaperProbitTradeService;
    }

    @Async
    public void connect(UpbitWebSocketTicket upbitWebSocketTicket, UpbitWebSocketType upbitWebSocketType) {

        log.info("Connecting to Upbit Web Socket Server...");

        List sendBody = Lists.newArrayList(upbitWebSocketTicket, upbitWebSocketType);
        String body = new Gson().toJson(sendBody);

        log.info("Upbit send message: {}", body);

        WebSocketClient webSocketClient;

        try {
            webSocketClient = new StandardWebSocketClient();

            webSocketSession =
                    webSocketClient.doHandshake(new UpbitReaperWebSocketHandler(reaperProbitTradeService, reaperOkexTradeService), new WebSocketHttpHeaders(), URI.create(tradeUrlProperties.getUpbitWebSockUrl())).get();

            try {
                TextMessage message = new TextMessage(body);
                webSocketSession.sendMessage(message);
                log.info("Upbit Client successfully sent ticker subscription message");
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
