package com.hanaset.tacogenji.scheduler;

import com.hanaset.tacocommon.utils.DateTimeUtils;
import com.hanaset.tacogenji.api.upbit.UpbitGenjiWebSocketService;
import com.hanaset.tacogenji.service.CryptoSelectService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UpbitTransactionScheduler {

    private final CryptoSelectService cryptoSelectService;
    private final UpbitGenjiWebSocketService upbitGenjiWebSocketService;

    public UpbitTransactionScheduler(CryptoSelectService cryptoSelectService,
                                     UpbitGenjiWebSocketService upbitGenjiWebSocketService) {
        this.cryptoSelectService = cryptoSelectService;
        this.upbitGenjiWebSocketService = upbitGenjiWebSocketService;
    }

    @Scheduled(cron = "1 5 0 * * *")
    public void upbitWebsocketClientConnect() {
        String pair = cryptoSelectService.getPair(DateTimeUtils.getCurrentBeforeNDay("yyyy-MM-dd", "Asia/Seoul", 3), DateTimeUtils.getCurrentDay("Asia/Seoul"));
        upbitGenjiWebSocketService.orderbookConnect(pair);
    }

    @Scheduled(cron = "1 0 0 * * *")
    public void upbitWebsocketClientDisconnect() {
        upbitGenjiWebSocketService.orderbookDisconnect();
    }
}
