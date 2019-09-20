package com.hanaset.taco.scheduler;

import com.hanaset.taco.api.upbit.model.UpbitOrderResponse;
import com.hanaset.taco.cache.UpbitTransactionCached;
import com.hanaset.taco.service.upbit.UpbitBalanceService;
import com.hanaset.taco.service.upbit.UpbitTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
@Component
public class TacoScheduler {

    private final UpbitBalanceService upbitBalanceService;

    private final UpbitTransactionService upbitTransactionService;

    public TacoScheduler(UpbitBalanceService upbitBalanceService,
                         UpbitTransactionService upbitTransactionService) {
        this.upbitBalanceService = upbitBalanceService;
        this.upbitTransactionService = upbitTransactionService;
    }

    @Scheduled(fixedDelay = 1000 * 5)
    public synchronized void lockCheck() {

        try {
            if (UpbitTransactionCached.LOCK) {
                System.out.println("Lock");
                Thread.sleep(1000 * 10);
                try {
                    Response<UpbitOrderResponse> response = upbitTransactionService.orderDeleting(UpbitTransactionCached.TICKET.getUuid());
                    if(response.isSuccessful()) {
                        log.info("scheduler 매수 취소:{}", response.body().toString());
                    }else {
                        log.error("scheduler 매수 취소 에러:{}", response.errorBody().byteString().toString());
                    }

                }catch(IOException e) {
                    log.error("scheduler Delete error");
                }
                UpbitTransactionCached.LOCK = false;
                UpbitTransactionCached.TICKET = null;
                System.out.println("UnLock");
            }
        }catch (InterruptedException e) {
            log.error("sleep error");
        }
    }

    @Scheduled(fixedDelay = 1000 * 30)
    public void balanceCheck() {
        upbitTransactionService.exchangeProfit();
    }

}
