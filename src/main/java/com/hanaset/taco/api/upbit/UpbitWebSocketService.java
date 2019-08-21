package com.hanaset.taco.api.upbit;

import com.google.common.collect.Lists;
import com.hanaset.taco.api.upbit.model.body.Ticket;
import com.hanaset.taco.api.upbit.model.body.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class UpbitWebSocketService {

    private final UpbitApiWebSocketClient upbitApiWebSocketClient;

    public UpbitWebSocketService(UpbitApiWebSocketClient upbitApiWebSocketClient) {
        this.upbitApiWebSocketClient = upbitApiWebSocketClient;
    }

    @PostConstruct
    public void connect() {

        Ticket ticket = Ticket.builder()
                .ticket("TACO_TEST")
                .build();

        Type type = Type.builder()
                .type("orderbook")
                .codes(Lists.newArrayList("KRW-BTC"))
                .build();


        upbitApiWebSocketClient.connect(ticket, type);
    }
}
