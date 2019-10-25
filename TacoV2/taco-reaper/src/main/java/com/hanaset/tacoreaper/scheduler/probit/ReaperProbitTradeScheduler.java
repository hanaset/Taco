package com.hanaset.tacoreaper.scheduler.probit;

import com.hanaset.tacoreaper.cached.ReaperTradeCached;
import com.hanaset.tacoreaper.service.probit.ReaperProbitTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ReaperProbitTradeScheduler {

    private ThreadPoolTaskScheduler scheduler;
    private ReaperProbitTradeService reaperProbitTradeService;

    public ReaperProbitTradeScheduler(ReaperProbitTradeService reaperProbitTradeService) {
        this.reaperProbitTradeService = reaperProbitTradeService;
    }

    public void stopScheduler() {
        scheduler.shutdown();
        log.info("<======================== Probit Scheduler Shutdown =======================>");
    }

    public void startScheduler(String pair) {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        reaperProbitTradeService.init(pair);
        scheduler.schedule(getRunnable(pair), getTrigger());
        log.info("<======================== Probit Scheduler Start =======================>");
    }

    private Runnable getRunnable(String pair) {
        //return () -> reaperProbitTradeService.tradeFlashingProbit(pair);
        return () -> reaperProbitTradeService.tradeProbit(pair);
    }

    private Trigger getTrigger() {
        return new PeriodicTrigger(ReaperTradeCached.TRADE_CONDITION.getInterval(), TimeUnit.SECONDS);
    }
}
