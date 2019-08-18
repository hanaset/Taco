package com.hanaset.taco.scheduler;

import com.hanaset.taco.service.TickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//@ConditionalOnProperty(
//        prefix = "taco.ticker.scheduler", name = "enabled", havingValue = "true"
//)
public class TacoScheduler {

    private final TickerService tickerService;

    public TacoScheduler(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    @Scheduled(fixedRate = 1000)
    public void tickerTask() {
        tickerService.getTicekrList();
    }
}
