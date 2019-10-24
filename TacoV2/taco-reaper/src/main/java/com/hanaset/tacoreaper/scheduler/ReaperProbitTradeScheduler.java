package com.hanaset.tacoreaper.scheduler;

import com.hanaset.tacocommon.utils.PairUtils;
import com.hanaset.tacoreaper.service.ReaperProbitTradeService;
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
        reaperProbitTradeService.init();
        scheduler.schedule(getRunnable(pair), getTrigger());
        log.info("<======================== Probit Scheduler Start =======================>");
    }

    private Runnable getRunnable(String pair) {
        return () -> reaperProbitTradeService.tradeProbit(PairUtils.getPair(pair));
    }

    private Trigger getTrigger() {
        return new PeriodicTrigger(1, TimeUnit.SECONDS);
    }
}
