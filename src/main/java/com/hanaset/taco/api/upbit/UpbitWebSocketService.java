package com.hanaset.taco.api.upbit;

import com.hanaset.taco.api.upbit.model.body.Ticket;
import com.hanaset.taco.api.upbit.model.body.Type;
import com.hanaset.taco.config.CryptoPairs;
import com.hanaset.taco.service.upbit.UpbitMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
public class UpbitWebSocketService {

    private final UpbitApiRestClient upbitApiRestClient;
    private final UpbitApiWebSocketClient upbitApiWebSocketClient;
    private final UpbitMarketService upbitMarketService;


    public UpbitWebSocketService(UpbitApiWebSocketClient upbitApiWebSocketClient,
                                 UpbitApiRestClient upbitApiRestClient,
                                 UpbitMarketService upbitMarketService) {
        this.upbitApiWebSocketClient = upbitApiWebSocketClient;
        this.upbitApiRestClient = upbitApiRestClient;
        this.upbitMarketService = upbitMarketService;
    }

    @PostConstruct
    public void trade_Connect() {


        List<String> pairs = upbitMarketService.getPairs();
        if(pairs.isEmpty())
            pairs = upbitMarketService.initPairs();

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_TRADE")
                .build();

        Type type = Type.builder()
                .type("trade")
                //.codes(CryptoPairs.UPBIT_PAIRS)
                .codes(pairs)
                .build();

        upbitApiWebSocketClient.connect(ticket, type);
    }

    @PostConstruct
    public void orderbook_Connect() {

        List<String> pairs = upbitMarketService.getPairs();
        if(pairs.isEmpty())
            pairs = upbitMarketService.initPairs();

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        Type type = Type.builder()
                .type("orderbook")
                //.codes(CryptoPairs.UPBIT_PAIRS)
                .codes(pairs)
                .build();

        upbitApiWebSocketClient.connect(ticket, type);
    }
}
