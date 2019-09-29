package com.hanaset.tacogenji.api.upbit;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.upbit.model.body.Ticket;
import com.hanaset.tacocommon.api.upbit.model.body.Type;
import com.hanaset.tacocommon.utils.DateTimeUtils;
import com.hanaset.tacogenji.service.CryptoSelectService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UpbitGenjiWebSocketService {

    private final UpbitGenjiWebSocketClient upbitGenjiWebSocketClient;
    private final CryptoSelectService cryptoSelectService;

    public UpbitGenjiWebSocketService(UpbitGenjiWebSocketClient upbitGenjiWebSocketClient,
                                      CryptoSelectService cryptoSelectService) {
        this.upbitGenjiWebSocketClient = upbitGenjiWebSocketClient;
        this.cryptoSelectService = cryptoSelectService;
    }

    @PostConstruct
    public void orderbook_Connect() {

        System.out.println("WebSocket Connecting ~ =============>");

        String pair = cryptoSelectService.getPair(DateTimeUtils.getCurrentBeforeNDay("yyyy-MM-DD", "Asia/Seoul", 3), DateTimeUtils.getCurrentDay("Asia/Seoul"));

        Ticket ticket = Ticket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        Type type = Type.builder()
                .type("orderbook")
                .codes(Lists.newArrayList("KRW-" + pair, "BTC-" + pair, "KRW-BTC"))
                .build();


        upbitGenjiWebSocketClient.connect(ticket, type);
    }
}
