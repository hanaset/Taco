package com.hanaset.tacomccree.api.upbit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderBook;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.tacocommon.cache.OrderbookCached;
import com.hanaset.tacomccree.config.PairConfig;
import com.hanaset.tacomccree.service.upbit.McCreeUpbitTradeService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class UpbitMcCreeWebSocketHandler extends BinaryWebSocketHandler {

    private final McCreeUpbitTradeService mcCreeUpbitTradeService;
    private final Map<String, PairConfig> pairConfigs;

    public UpbitMcCreeWebSocketHandler(McCreeUpbitTradeService mcCreeUpbitTradeService,
                                       Map<String, PairConfig> pairConfigs) {
        this.mcCreeUpbitTradeService = mcCreeUpbitTradeService;
        this.pairConfigs = pairConfigs;
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

                UpbitOrderbookItem newItem = upbitOrderBook.getOrderbook_units().get(0);
                UpbitOrderbookItem item = OrderbookCached.UPBIT.get(upbitOrderBook.getCode());

//                if(item == null || !item.getAsk_price().equals(newItem.getAsk_price()) || !item.getBid_price().equals(newItem.getBid_price())) {
//                    log.info("[{}] 가격 변동 : {} / {}", upbitOrderBook.getCode(), newItem.getBid_price(), newItem.getAsk_price());
//                    OrderbookCached.UPBIT_CHANGE.put(upbitOrderBook.getCode(), true);
//                }else {
//                    OrderbookCached.UPBIT_CHANGE.put(upbitOrderBook.getCode(), false);
//                }
//
//                OrderbookCached.UPBIT.put(upbitOrderBook.getCode(), newItem);

                Boolean change = false;
                if (item == null || !item.getAsk_price().equals(newItem.getAsk_price()) || !item.getBid_price().equals(newItem.getBid_price())) {
                    log.info("[{}] 가격 변동 : {} [{}] / {} [{}]", upbitOrderBook.getCode(), newItem.getBid_price(), pairConfigs.get(upbitOrderBook.getCode()).getLimitPrice().divide(BigDecimal.valueOf(newItem.getBid_price()), RoundingMode.HALF_UP).toPlainString(),
                            newItem.getAsk_price(), pairConfigs.get(upbitOrderBook.getCode()).getLimitPrice().divide(BigDecimal.valueOf(newItem.getAsk_price()), RoundingMode.HALF_UP).toPlainString());
                    change = true;
                    OrderbookCached.UPBIT.put(upbitOrderBook.getCode(), newItem);
                }

                mcCreeUpbitTradeService.webSocketTrade(pairConfigs.get(upbitOrderBook.getCode()), change);
            }

        } catch (
                JsonParseException e) {
            log.error(e.getMessage());
        } catch (
                IOException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Upbit Web Socket Server! Session - " + session);
    }
}
