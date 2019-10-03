package com.hanaset.tacogenji.scheduler;

import com.hanaset.tacocommon.utils.DateTimeUtils;
import com.hanaset.tacogenji.api.upbit.UpbitGenjiWebSocketService;
import com.hanaset.tacogenji.service.CryptoSelectService;
import com.hanaset.tacogenji.service.UpbitBalanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UpbitTransactionScheduler {

    private final CryptoSelectService cryptoSelectService;
    private final UpbitGenjiWebSocketService upbitGenjiWebSocketService;
    private final UpbitBalanceService upbitBalanceService;

    public UpbitTransactionScheduler(CryptoSelectService cryptoSelectService,
                                     UpbitGenjiWebSocketService upbitGenjiWebSocketService,
                                     UpbitBalanceService upbitBalanceService) {
        this.cryptoSelectService = cryptoSelectService;
        this.upbitGenjiWebSocketService = upbitGenjiWebSocketService;
        this.upbitBalanceService = upbitBalanceService;
    }

    @Scheduled(cron = "1 5 0 * * *", zone = "Asia/Seoul")
    public void upbitWebsocketClientConnect() {
        String pair = cryptoSelectService.getPair(DateTimeUtils.getCurrentBeforeNDay("yyyy-MM-dd", "Asia/Seoul", 2), DateTimeUtils.getCurrentDay("Asia/Seoul"));
        upbitBalanceService.startBalance(pair);
        upbitGenjiWebSocketService.orderbookConnect(pair);
    }

    @Scheduled(cron = "1 0 0 * * *", zone = "Asia/Seoul")
    public void upbitWebsocketClientDisconnect() {
        upbitGenjiWebSocketService.orderbookDisconnect();
        upbitBalanceService.initBalance();
    }
}
