package com.hanaset.taco.api.upbit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaset.taco.api.upbit.model.UpbitOrderBook;
import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.service.UpbitAskCheckService;
import com.hanaset.taco.utils.Taco2CurrencyConvert;
import com.hanaset.taco.utils.Taco2JsonConvert;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class UpbitWebSocketHandler extends BinaryWebSocketHandler {

    @Autowired
    private UpbitAskCheckService upbitAskCheckService;

    public UpbitWebSocketHandler(UpbitAskCheckService upbitAskCheckService) {
        this.upbitAskCheckService = upbitAskCheckService;
    }

    @Override
    @Async
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer byteMessage = message.getPayload();
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteMessage);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            UpbitOrderBook upbitOrderBook = objectMapper.readValue(charBuffer.toString(), UpbitOrderBook.class);

            if (OrderbookCached.UPBIT_BTC != null) {

                if (upbitOrderBook.getCode().contains("BTC-")) { //BTC일 경우

                    UpbitOrderbookItem item = upbitOrderBook.getOrderbook_units().get(0);
                    //item.setAsk_price(Taco2CurrencyConvert.convertBTC2KRW(item.getAsk_price()));
                    //item.setBid_price(Taco2CurrencyConvert.convertBTC2KRW(item.getBid_price()));

                    OrderbookCached.UPBIT.put(upbitOrderBook.getCode(), item);
                    //log.info("code -> {}, orderbook -> {}", upbitOrderBook.getCode(), OrderbookCached.UPBIT.get(upbitOrderBook.getCode()));


                } else { // KRW일 경우
                    OrderbookCached.UPBIT.put(upbitOrderBook.getCode(), upbitOrderBook.getOrderbook_units().get(0));
                    //log.info("code -> {}, orderbook -> {}", upbitOrderBook.getCode(), OrderbookCached.UPBIT.get(upbitOrderBook.getCode()));
                }

                upbitAskCheckService.compareASKWithBID("ETH");

            }
        } catch (JsonParseException e) {
            log.error(e.getMessage());
        } catch (JsonMappingException e) { // Orderbook 외의 웹소켓 통신 데이터

            JSONObject object = Taco2JsonConvert.convertJSONObject(charBuffer.toString());

            if (object.get("type").toString().equals("ticker")) {
                OrderbookCached.UPBIT_BTC = new BigDecimal(object.get("trade_price").toString());
                //log.info("BTC price : {} KRW", OrderbookCached.UPBIT_BTC.toPlainString());
            } else {
                log.error(e.getMessage());
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Upbit Web Socket Server! Session - " + session);
    }
}
