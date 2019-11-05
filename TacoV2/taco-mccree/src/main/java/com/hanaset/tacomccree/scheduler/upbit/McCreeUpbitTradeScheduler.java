package com.hanaset.tacomccree.scheduler.upbit;

import com.hanaset.tacomccree.config.PairConfig;
import com.hanaset.tacomccree.service.upbit.McCreeUpbitTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

@Slf4j
public class McCreeUpbitTradeScheduler {

    private ThreadPoolTaskScheduler scheduler;
    private McCreeUpbitTradeService mcCreeUpbitTradeService;
    private PairConfig pairConfig;

    public McCreeUpbitTradeScheduler(McCreeUpbitTradeService mcCreeUpbitTradeService) {
        this.mcCreeUpbitTradeService = mcCreeUpbitTradeService;
    }

    public void stopScheduler() {
        scheduler.shutdown();
        mcCreeUpbitTradeService.init(pairConfig);
        log.info("<======================== Upbit {} Scheduler Shutdown =======================>", pairConfig.getAsset());
    }

    public ThreadPoolTaskScheduler startScheduler(PairConfig pairConfig) {
        this.pairConfig = pairConfig;
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        mcCreeUpbitTradeService.init(pairConfig);
        scheduler.schedule(getRunnable(pairConfig), getTrigger(pairConfig));
        log.info("<======================== Upbit {} Scheduler Start =======================>", pairConfig.getAsset());
        return scheduler;
    }

    private Runnable getRunnable(PairConfig pairConfig) {
        return () -> mcCreeUpbitTradeService.trade(pairConfig);
    }

    private Trigger getTrigger(PairConfig pairConfig) {
        return new PeriodicTrigger(pairConfig.getInterval(), TimeUnit.SECONDS);
    }
}
