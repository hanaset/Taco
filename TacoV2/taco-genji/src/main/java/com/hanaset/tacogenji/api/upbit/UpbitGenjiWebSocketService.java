package com.hanaset.tacogenji.api.upbit;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketTicket;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpbitGenjiWebSocketService {

    private final UpbitGenjiWebSocketClient upbitGenjiWebSocketClient;

    public UpbitGenjiWebSocketService(UpbitGenjiWebSocketClient upbitGenjiWebSocketClient) {
        this.upbitGenjiWebSocketClient = upbitGenjiWebSocketClient;
    }

    public void orderbookConnect(String pair) {

        log.info("<======================== WebSocket Connecting =======================>");

        UpbitWebSocketTicket upbitWebSocketTicket = UpbitWebSocketTicket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        UpbitWebSocketType upbitWebSocketType = UpbitWebSocketType.builder()
                .type("orderbook")
                .codes(Lists.newArrayList("KRW-" + pair, "BTC-" + pair, "KRW-BTC"))
                .build();


        upbitGenjiWebSocketClient.connect(upbitWebSocketTicket, upbitWebSocketType);
    }

    public void orderbookDisconnect() {

        upbitGenjiWebSocketClient.disconnect();

    }
}
