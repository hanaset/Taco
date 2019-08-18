package com.hanaset.taco.scheduler;

import com.hanaset.taco.service.Chart60MService;
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
    private final Chart60MService chart60MService;

    public TacoScheduler(TickerService tickerService,
                         Chart60MService chart60MService) {
        this.tickerService = tickerService;
        this.chart60MService = chart60MService;
    }

    @Scheduled(fixedRate = 1000)
    public void tickerTask() {
        tickerService.getTicekrList();
    }

    @Scheduled(cron = "1 0 0 * * *")
    public void chart60MTask() {
        chart60MService.getChartData();
    }
}
