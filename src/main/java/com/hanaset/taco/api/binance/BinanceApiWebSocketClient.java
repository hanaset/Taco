package com.hanaset.taco.api.binance;

import com.hanaset.taco.api.binance.domain.event.AggTradeEvent;
import com.hanaset.taco.api.binance.domain.event.CandlestickEvent;
import com.hanaset.taco.api.binance.domain.event.DepthEvent;
import com.hanaset.taco.api.binance.domain.event.UserDataUpdateEvent;
import com.hanaset.taco.api.binance.domain.market.CandlestickInterval;

/**
 * Binance API data streaming façade, supporting streaming of events through web sockets.
 */
public interface BinanceApiWebSocketClient {

  void onDepthEvent(String symbol, BinanceApiCallback<DepthEvent> callback);

  void onCandlestickEvent(String symbol, CandlestickInterval interval, BinanceApiCallback<CandlestickEvent> callback);

  void onAggTradeEvent(String symbol, BinanceApiCallback<AggTradeEvent> callback);

  void onUserDataUpdateEvent(String listenKey, BinanceApiCallback<UserDataUpdateEvent> callback);
}
