package com.hanaset.taco.api.upbit;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hanaset.taco.api.upbit.model.body.Ticket;
import com.hanaset.taco.api.upbit.model.body.Type;
import com.hanaset.taco.properties.TradeUrlProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class UpbitApiWebSocketClient {

    private final TradeUrlProperties tradeUrlProperties;

    public UpbitApiWebSocketClient(TradeUrlProperties tradeUrlProperties) {
        this.tradeUrlProperties = tradeUrlProperties;
    }

    public void connect(Ticket ticket, Type type) {

        log.info("Connecting to Upbit Web Socket Server...");

        List sendBody = Lists.newArrayList(ticket, type);
        String body = new Gson().toJson(sendBody);

        log.info("Upbit send message: {}", body);

        WebSocketClient webSocketClient;

        try {
            webSocketClient = new StandardWebSocketClient();

            WebSocketSession webSocketSession = webSocketClient.doHandshake(new BinaryWebSocketHandler() {

                @Override
                public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
                    ByteBuffer byteMessage= message.getPayload();
                    CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteMessage);

                    log.info("Upbit client response -> {}", charBuffer.toString());
                }

                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("Connected to Upbit Web Socket Server! Session - " + session);
                }
            }, new WebSocketHttpHeaders(), URI.create(tradeUrlProperties.getUpbitWebSockUrl())).get();

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
