package com.hanaset.tacomccree.api.upbit;

import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketTicket;
import com.hanaset.tacocommon.api.upbit.model.body.UpbitWebSocketType;
import com.hanaset.tacomccree.config.PairConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UpbitMcCreeWebSocketService {

    private final UpbitMcCreeWebSocketClient upbitMccreeWebSocketClient;

    public UpbitMcCreeWebSocketService(UpbitMcCreeWebSocketClient upbitMccreeWebSocketClient) {
        this.upbitMccreeWebSocketClient = upbitMccreeWebSocketClient;
    }

    public void orderbookConnect(List<String> pairs, Map<String, PairConfig> pairConfigs) {

        log.info("<======================== WebSocket Connecting =======================>");

        UpbitWebSocketTicket upbitWebSocketTicket = UpbitWebSocketTicket.builder()
                .ticket("UPBIT_ORDERBOOK")
                .build();

        UpbitWebSocketType upbitWebSocketType = UpbitWebSocketType.builder()
                .type("orderbook")
                .codes(pairs)
                .build();


        upbitMccreeWebSocketClient.connect(upbitWebSocketTicket, upbitWebSocketType, pairConfigs);
    }

    public void orderbookDisconnect() {

        upbitMccreeWebSocketClient.disconnect();

    }
}
