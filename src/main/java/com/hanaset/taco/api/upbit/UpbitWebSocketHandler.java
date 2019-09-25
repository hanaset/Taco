package com.hanaset.taco.api.upbit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaset.taco.api.upbit.model.UpbitOrderBook;
import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.taco.api.upbit.model.UpbitTrade;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.service.upbit.UpbitMarketTransactionService;
import com.hanaset.taco.service.upbit.UpbitTransactionService;
import com.hanaset.taco.utils.Taco2UpbitConvert;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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

    private UpbitTransactionService upbitTransactionService;
    private UpbitMarketTransactionService upbitMarketTransactionService;

    public UpbitWebSocketHandler(UpbitTransactionService upbitTransactionService,
                                 UpbitMarketTransactionService upbitMarketTransactionService) {
        this.upbitTransactionService = upbitTransactionService;
        this.upbitMarketTransactionService = upbitMarketTransactionService;
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer byteMessage = message.getPayload();
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteMessage);

        JSONObject jsonObject = (JSONObject)JSONValue.parse(charBuffer.toString());
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            if(jsonObject.get("type").equals("orderbook")) {

                UpbitOrderBook upbitOrderBook = objectMapper.readValue(charBuffer.toString(), UpbitOrderBook.class);

                if (upbitOrderBook.getCode().equals("KRW-BTC")) {
                    OrderbookCached.UPBIT_BTC.put("bid", BigDecimal.valueOf(upbitOrderBook.getOrderbook_units().get(0).getBid_price()));
                    OrderbookCached.UPBIT_BTC.put("ask", BigDecimal.valueOf(upbitOrderBook.getOrderbook_units().get(0).getAsk_price()));
                    return;
                }

                if (!OrderbookCached.UPBIT_BTC.isEmpty()) {

                    UpbitOrderbookItem item = upbitOrderBook.getOrderbook_units().get(0);
                    OrderbookCached.UPBIT.put(upbitOrderBook.getCode(), item);
                    //System.out.println(upbitOrderBook);
                    //upbitTransactionService.checkProfit(Taco2UpbitConvert.convertPair(upbitOrderBook.getCode()));
                    upbitMarketTransactionService.checkProfit(Taco2UpbitConvert.convertPair(upbitOrderBook.getCode()));
                }
            }else if(jsonObject.get("type").equals("trade")) {

                UpbitTrade upbitTrade = objectMapper.readValue(charBuffer.toString(), UpbitTrade.class);
                //upbitTransactionService.orderProfit(upbitTrade);
                upbitMarketTransactionService.orderProfit(upbitTrade);

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
