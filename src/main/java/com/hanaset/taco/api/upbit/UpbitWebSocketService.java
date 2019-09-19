package com.hanaset.taco.api.upbit;

import com.hanaset.taco.api.upbit.model.body.Ticket;
import com.hanaset.taco.api.upbit.model.body.Type;
import com.hanaset.taco.config.CryptoPairs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class UpbitWebSocketService {

    private final UpbitApiRestClient upbitApiRestClient;
    private final UpbitApiWebSocketClient upbitApiWebSocketClient;

    private Map<String, String> pairs;

    public UpbitWebSocketService(UpbitApiWebSocketClient upbitApiWebSocketClient,
                                 UpbitApiRestClient upbitApiRestClient) {
        this.upbitApiWebSocketClient = upbitApiWebSocketClient;
        this.upbitApiRestClient = upbitApiRestClient;

//        pairs = Maps.newHashMap();
//
//        List<UpbitMarket>upbitMarkets =  this.upbitApiRestClient.getMarket().blockingGet();
//
//        for(UpbitMarket market : upbitMarkets) {
//
//        }
    }

    @PostConstruct
    public void trade_Connect() {

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_TRADE")
                .build();

        Type type = Type.builder()
                .type("trade")
                .codes(CryptoPairs.UPBIT_PAIRS)
                .build();

        upbitApiWebSocketClient.connect(ticket, type);
    }

    @PostConstruct
    public void orderbook_Connect() {

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        Type type = Type.builder()
                .type("orderbook")
                .codes(CryptoPairs.UPBIT_PAIRS)
                .build();

        upbitApiWebSocketClient.connect(ticket, type);
    }
}
