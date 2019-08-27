package com.hanaset.taco;

import com.hanaset.taco.api.binance.BinanceApiCallback;
import com.hanaset.taco.api.binance.BinanceApiClientFactory;
import com.hanaset.taco.api.binance.BinanceApiRestClient;
import com.hanaset.taco.api.binance.BinanceApiWebSocketClient;
import com.hanaset.taco.api.binance.domain.event.DepthEvent;
import com.hanaset.taco.api.binance.domain.event.UserDataUpdateEvent;
import com.hanaset.taco.api.binance.domain.market.OrderBookEntry;
import com.hanaset.taco.api.binance.exception.BinanceApiException;
import com.hanaset.taco.api.binance.utils.SymbolPairHelper;
import okhttp3.Response;
import okhttp3.WebSocket;

import java.util.List;

public class BinanceTest {
    public static void main(String[] args) {

        BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();
        BinanceApiRestClient restClient = BinanceApiClientFactory.newInstance(
                "srfSNPKMohrpETWDUrHKJLIkN3K09YE3CT8IR5r9udjTwEiN7vNXy0YyqzNDgIli",
                "VyUKvJHn5fO8KsEIisHRqSeCyHlIOtCDLJH3MDiNL1qpcboLcoCTJpjkOtvznbtE")
                .newRestClient();

        client.onDepthEvent(SymbolPairHelper.getBinanceSymbolPair("eth_usdt").toLowerCase(), new BinanceApiCallback<DepthEvent>() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {

            }

            @Override
            public void onResponse(DepthEvent response) throws BinanceApiException {
                List<OrderBookEntry> bids = response.getBids();
                List<OrderBookEntry> asks = response.getAsks();

//                if (bids.size() > 0 && asks.size() > 0) {
//                    System.out.println("BIDS ::::::>");
//                    System.out.println("price :: " + bids.get(0).getPrice());
//                    System.out.println("qty :: " + bids.get(0).getQty());
//
//                    System.out.println("ASKS ::::::>");
//                    System.out.println("price :: " + asks.get(0).getPrice());
//                    System.out.println("qty ::" + asks.get(0).getQty());
//                }


            }

            @Override
            public void onClosing() {

            }

            @Override
            public void onClosed() {

            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {

            }

            @Override
            public void terminate() {

            }
        });

        restClient.startUserDataStreamRx()
                .map(listenKey -> {
                    System.out.println(listenKey);
                    return listenKey.getListenKey();
                })
                .subscribe(listenKey -> client.onUserDataUpdateEvent(listenKey, new BinanceApiCallback<UserDataUpdateEvent>() {

                    WebSocket ws;

                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {

                        System.out.println("onOpen");

                        ws = webSocket;
                    }

                    @Override
                    public void onResponse(UserDataUpdateEvent response) throws BinanceApiException {

                        System.out.println("onResponse");

                        System.out.println(response);
                    }

                    @Override
                    public void onClosing() {
                        System.out.println("onClosing");

                    }

                    @Override
                    public void onClosed() {
                        System.out.println("onClosed");
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                        System.out.println("onFailure");
                        System.out.println(throwable.getMessage() + " ==> " + throwable);
                        System.out.println(response);
                    }

                    @Override
                    public void terminate() {

                    }
                }));
    }
}
