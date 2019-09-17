package com.hanaset.taco.api.upbit;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hanaset.taco.api.upbit.model.body.Ticket;
import com.hanaset.taco.api.upbit.model.body.Type;
import com.hanaset.taco.properties.TradeUrlProperties;
import com.hanaset.taco.service.upbit.UpbitTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class UpbitApiWebSocketClient {

    private final TradeUrlProperties tradeUrlProperties;
    private final UpbitTransactionService upbitTransactionService;

    public UpbitApiWebSocketClient(TradeUrlProperties tradeUrlProperties,
                                   UpbitTransactionService upbitTransactionService) {
        this.tradeUrlProperties = tradeUrlProperties;
        this.upbitTransactionService = upbitTransactionService;
    }

    public void connect(Ticket ticket, Type type) {

        log.info("Connecting to Upbit Web Socket Server...");

        List sendBody = Lists.newArrayList(ticket, type);
        String body = new Gson().toJson(sendBody);

        log.info("Upbit send message: {}", body);

        WebSocketClient webSocketClient;

        try {
            webSocketClient = new StandardWebSocketClient();

            WebSocketSession webSocketSession =
                    webSocketClient.doHandshake(new UpbitWebSocketHandler(upbitTransactionService), new WebSocketHttpHeaders(), URI.create(tradeUrlProperties.getUpbitWebSockUrl())).get();

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

}
