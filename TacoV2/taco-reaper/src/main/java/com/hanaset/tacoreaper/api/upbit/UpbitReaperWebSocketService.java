package com.hanaset.tacoreaper.api.upbit;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.upbit.model.body.Ticket;
import com.hanaset.tacocommon.api.upbit.model.body.Type;
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

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_TRADE")
                .build();

        Type type = Type.builder()
                .type("trade")
                .codes(Lists.newArrayList(pair))
                .build();


        upbitReaperWebSocketClient.connect(ticket, type);
    }

    public void disconncetTrade() {

        upbitReaperWebSocketClient.disconnect();

    }
}
