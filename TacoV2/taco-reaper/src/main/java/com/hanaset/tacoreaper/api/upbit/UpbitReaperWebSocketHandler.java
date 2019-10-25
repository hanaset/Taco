package com.hanaset.tacoreaper.api.upbit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderBook;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.tacocommon.cache.OrderbookCached;
import com.hanaset.tacoreaper.service.okex.ReaperOkexTradeService;
import com.hanaset.tacoreaper.service.probit.ReaperProbitTradeService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class UpbitReaperWebSocketHandler extends BinaryWebSocketHandler {

    private final ReaperProbitTradeService reaperProbitTradeService;
    private final ReaperOkexTradeService reaperOkexTradeService;

    public UpbitReaperWebSocketHandler(ReaperProbitTradeService reaperProbitTradeService,
                                       ReaperOkexTradeService reaperOkexTradeService) {
        this.reaperProbitTradeService = reaperProbitTradeService;
        this.reaperOkexTradeService = reaperOkexTradeService;
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer byteMessage = message.getPayload();
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteMessage);

        JSONObject jsonObject = (JSONObject) JSONValue.parse(charBuffer.toString());
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            if (jsonObject.get("type").equals("orderbook")) {

                UpbitOrderBook upbitOrderBook = objectMapper.readValue(charBuffer.toString(), UpbitOrderBook.class);

                UpbitOrderbookItem item = upbitOrderBook.getOrderbook_units().get(0);
                OrderbookCached.UPBIT.put(upbitOrderBook.getCode(), item);

                reaperOkexTradeService.updateUpbitData(item, upbitOrderBook.getCode());

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