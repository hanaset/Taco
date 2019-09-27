package com.hanaset.tacomercy.api.upbit;

import com.hanaset.tacocommon.api.upbit.model.body.Ticket;
import com.hanaset.tacocommon.api.upbit.model.body.Type;
import com.hanaset.tacomercy.service.UpbitMercyMarketService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class UpbitMercyWebSocketService {

    private final UpbitMercyWebSocketClient upbitMercyWebSocketClient;
    private final UpbitMercyMarketService upbitMercyMarketService;


    public UpbitMercyWebSocketService(UpbitMercyWebSocketClient upbitMercyWebSocketClient,
                                      UpbitMercyMarketService upbitMercyMarketService) {
        this.upbitMercyWebSocketClient = upbitMercyWebSocketClient;
        this.upbitMercyMarketService = upbitMercyMarketService;
    }

    @PostConstruct
    public void orderbook_Connect() {

        System.out.println("WebSocket Connecting ~ =============>");

        List<String> pairs = upbitMercyMarketService.getPairs();
        if(pairs.isEmpty())
            pairs = upbitMercyMarketService.initPairs();

        System.out.println(pairs);

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        Type type = Type.builder()
                .type("orderbook")
                .codes(pairs)
                .build();

        upbitMercyWebSocketClient.connect(ticket, type);
    }
}
