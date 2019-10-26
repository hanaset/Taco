package com.hanaset.tacomccree.api.upbit;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketTicket;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UpbitMcCreeWebSocketService {

    private final UpbitMcCreeWebSocketClient upbitMccreeWebSocketClient;

    public UpbitMcCreeWebSocketService(UpbitMcCreeWebSocketClient upbitMccreeWebSocketClient) {
        this.upbitMccreeWebSocketClient = upbitMccreeWebSocketClient;
    }

    public void orderbookConnect(List<String> pairs) {

        log.info("<======================== WebSocket Connecting =======================>");

        UpbitWebSocketTicket upbitWebSocketTicket = UpbitWebSocketTicket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        UpbitWebSocketType upbitWebSocketType = UpbitWebSocketType.builder()
                .type("orderbook")
                .codes(pairs)
                .build();


        upbitMccreeWebSocketClient.connect(upbitWebSocketTicket, upbitWebSocketType);
    }

    public void orderbookDisconnect() {

        upbitMccreeWebSocketClient.disconnect();

    }
}
