package com.hanaset.taco.upbit;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hanaset.taco.api.upbit.model.UpbitOrderBook;
import com.hanaset.taco.api.upbit.model.body.Ticket;
import com.hanaset.taco.api.upbit.model.body.Type;
import com.hanaset.taco.properties.TradeUrlProperties;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import org.springframework.context.annotation.Import;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

@Import(TradeUrlProperties.class)
public class UpbitWebsocketTests {

    public static void main(String[] args) throws URISyntaxException {

        UpbitApiWebSocketClient upbitApiWebSocketClient = new UpbitApiWebSocketClientImpl();

        upbitApiWebSocketClient.orderbook(new UpbitApiCallback<UpbitOrderBook>() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("onOpen");

                Ticket ticket = Ticket.builder()
                        .ticket("TACO_TEST")
                        .build();

                Type type = Type.builder()
                        .type("orderbook")
                        .codes(Lists.newArrayList("KRW-BTC"))
                        .build();

                List sendBody = Lists.newArrayList(ticket, type);

                String body = new Gson().toJson(sendBody);

                System.out.println(body);

                ByteString bodyByte = ByteString.encodeUtf8(body);
                String bo = bodyByte.string(Charset.defaultCharset());

//                webSocket.send(bo);
                webSocket.send(body);
            }

            @Override
            public void onResponse(UpbitOrderBook response) {
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
            public void onFailure(Throwable t, Response response) {
                System.out.println("onFailuer");
                System.out.println("throwable : " + t);
                System.out.println(response);
            }
        });

    }
}

