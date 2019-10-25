package com.hanaset.tacoreaper.api.upbit;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketTicket;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpbitReaperWebSocketService {

    private final UpbitReaperWebSocketClient upbitReaperWebSocketClient;

    public UpbitReaperWebSocketService(UpbitReaperWebSocketClient upbitReaperWebSocketClient) {
        this.upbitReaperWebSocketClient = upbitReaperWebSocketClient;
    }

    public void conncentTrade(String pair) {

        log.info("<======================== WebSocket Connecting =======================>");

        UpbitWebSocketTicket upbitWebSocketTicket = UpbitWebSocketTicket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        UpbitWebSocketType upbitWebSocketType = UpbitWebSocketType.builder()
                .type("orderbook")
                .codes(Lists.newArrayList(pair))
                .build();


        upbitReaperWebSocketClient.connect(upbitWebSocketTicket, upbitWebSocketType);
    }

    public void disconncetTrade() {

        upbitReaperWebSocketClient.disconnect();

    }
}
