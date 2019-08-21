package com.hanaset.taco.scheduler;

import com.hanaset.taco.service.Chart60MService;
import com.hanaset.taco.service.TickerService;
import com.hanaset.taco.service.TransactionHistoryService;
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

    private final TickerService tickerService;
    private final Chart60MService chart60MService;
    private final TransactionHistoryService transactionHistoryService;

    public TacoScheduler(TickerService tickerService,
                         Chart60MService chart60MService,
                         TransactionHistoryService transactionHistoryService) {
        this.tickerService = tickerService;
        this.chart60MService = chart60MService;
        this.transactionHistoryService = transactionHistoryService;
    }

    @Scheduled(cron = "1 0 0 * * *")
    public void tickerTask() {
        tickerService.getTicekrList();
    }

    @Scheduled(fixedRate = 1000)
    public void transactionHistoryTask() {
        transactionHistoryService.getTransactionHistory();
    }

}
