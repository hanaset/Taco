package com.hanaset.tacogenji.api.okex;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.okex.model.body.OkexWebSocketOp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OkexGenjiWebSocketService {

    private final OkexGenjiWebSocketClient okexGenjiWebSocketClient;

    public OkexGenjiWebSocketService(OkexGenjiWebSocketClient okexGenjiWebSocketClient) {
        this.okexGenjiWebSocketClient = okexGenjiWebSocketClient;
    }

    public void orderbookConnect(String pair) {

        log.info("<======================== WebSocket Connecting =======================>");

        OkexWebSocketOp op = OkexWebSocketOp.builder()
                .op("subscribe")
                .args(Lists.newArrayList("spot/ticker:ETH-USDT"))
                .build();

        okexGenjiWebSocketClient.connect(op);
    }

    public void orderbookDisconnect() {

        okexGenjiWebSocketClient.disconnect();

    }
}
