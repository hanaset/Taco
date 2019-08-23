package com.hanaset.taco.scheduler;

import com.hanaset.taco.service.upbit.UpbitBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "taco.ticker.scheduler", name = "enabled", havingValue = "true"
)
public class TacoScheduler {

    private final UpbitBalanceService upbitBalanceService;

    public TacoScheduler(UpbitBalanceService upbitBalanceService) {
        this.upbitBalanceService = upbitBalanceService;
    }

    @Scheduled(cron = "* */15 * * * *")
    public void recordUpbitBalance() {
        upbitBalanceService.recordBalance();
    }

}
