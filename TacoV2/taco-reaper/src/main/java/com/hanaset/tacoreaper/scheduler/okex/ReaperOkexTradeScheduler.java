package com.hanaset.tacoreaper.scheduler.okex;

import com.hanaset.tacoreaper.cached.ReaperTradeCached;
import com.hanaset.tacoreaper.service.okex.ReaperOkexTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ReaperOkexTradeScheduler {

    private ThreadPoolTaskScheduler scheduler;
    private ReaperOkexTradeService reaperOkexTradeService;

    public ReaperOkexTradeScheduler(ReaperOkexTradeService reaperOkexTradeService) {
        this.reaperOkexTradeService = reaperOkexTradeService;
    }

    public void stopScheduler() {
        scheduler.shutdown();
        log.info("<======================== Okex Scheduler Shutdown =======================>");
    }

    public void startScheduler(String pair) {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        reaperOkexTradeService.init(pair);
        scheduler.schedule(getRunnable(pair), getTrigger());
        log.info("<======================== Okex Scheduler Start =======================>");
    }

    private Runnable getRunnable(String pair) {
        //return () -> reaperProbitTradeService.tradeFlashingProbit(pair);
        return () -> reaperOkexTradeService.tradeProbit(pair);
    }

    private Trigger getTrigger() {
        return new PeriodicTrigger(ReaperTradeCached.TRADE_CONDITION.getInterval(), TimeUnit.SECONDS);
    }
}
