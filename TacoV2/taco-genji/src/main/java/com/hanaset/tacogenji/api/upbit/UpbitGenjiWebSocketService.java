package com.hanaset.tacogenji.api.upbit;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.upbit.model.body.Ticket;
import com.hanaset.tacocommon.api.upbit.model.body.Type;
import com.hanaset.tacocommon.utils.DateTimeUtils;
import com.hanaset.tacogenji.service.CryptoSelectService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class UpbitGenjiWebSocketService {

    private final UpbitGenjiWebSocketClient upbitGenjiWebSocketClient;

    public UpbitGenjiWebSocketService(UpbitGenjiWebSocketClient upbitGenjiWebSocketClient) {
        this.upbitGenjiWebSocketClient = upbitGenjiWebSocketClient;
    }

    public void orderbookConnect(String pair) {

        log.info("<======================== WebSocket Connecting =======================>");

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        Type type = Type.builder()
                .type("orderbook")
                .codes(Lists.newArrayList("KRW-" + pair, "BTC-" + pair, "KRW-BTC"))
                .build();


        upbitGenjiWebSocketClient.connect(ticket, type);
    }

    public void orderbookDisconnect() {

        upbitGenjiWebSocketClient.disconnect();

    }
}
