package com.hanaset.tacoreaper.api.upbit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderBook;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.tacocommon.api.upbit.model.UpbitTicket;
import com.hanaset.tacocommon.api.upbit.model.UpbitTrade;
import com.hanaset.tacocommon.cache.OrderbookCached;
import com.hanaset.tacocommon.utils.Taco2UpbitConvert;
import com.hanaset.tacoreaper.service.ReaperProbitTradeService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class UpbitReaperWebSocketHandler extends BinaryWebSocketHandler {

    private final ReaperProbitTradeService reaperProbitTradeService;

    public UpbitReaperWebSocketHandler(ReaperProbitTradeService reaperProbitTradeService) {
        this.reaperProbitTradeService = reaperProbitTradeService;
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer byteMessage = message.getPayload();
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteMessage);

        JSONObject jsonObject = (JSONObject) JSONValue.parse(charBuffer.toString());
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            if (jsonObject.get("type").equals("trade")) {

                UpbitTrade upbitTrade = objectMapper.readValue(charBuffer.toString(), UpbitTrade.class);
                reaperProbitTradeService.updateUpbitData(upbitTrade);
            }

        } catch (JsonParseException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Upbit Web Socket Server! Session - " + session);
    }
}