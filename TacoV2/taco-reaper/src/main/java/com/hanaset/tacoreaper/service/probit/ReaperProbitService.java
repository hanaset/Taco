package com.hanaset.tacoreaper.service.probit;

import com.hanaset.tacocommon.api.probit.ProbitApiRestClient;
import com.hanaset.tacocommon.api.probit.model.ProbitBalance;
import com.hanaset.tacocommon.api.probit.model.ProbitMarketResponse;
import com.hanaset.tacocommon.utils.PairUtils;
import com.hanaset.tacoreaper.api.upbit.UpbitReaperWebSocketService;
import com.hanaset.tacoreaper.scheduler.probit.ReaperProbitTradeScheduler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReaperProbitService {

    private final ProbitApiRestClient probitApiRestClient;
    private final UpbitReaperWebSocketService upbitReaperWebSocketService;
    private final ReaperProbitTradeService reaperProbitTradeService;
    private ReaperProbitTradeScheduler reaperProbitTradeScheduler;

    public ReaperProbitService(ProbitApiRestClient probitApiRestClient,
                               UpbitReaperWebSocketService upbitReaperWebSocketService,
                               ReaperProbitTradeService reaperProbitTradeService) {
        this.probitApiRestClient = probitApiRestClient;
        this.upbitReaperWebSocketService = upbitReaperWebSocketService;
        this.reaperProbitTradeService = reaperProbitTradeService;
    }

    public void serviceStart(String pair) {
        upbitReaperWebSocketService.conncentTrade(pair);
        reaperProbitTradeScheduler = new ReaperProbitTradeScheduler(reaperProbitTradeService);
        reaperProbitTradeScheduler.startScheduler(PairUtils.getPair(pair));

    }

    public void serviceFinish() {
        upbitReaperWebSocketService.disconncetTrade();
        reaperProbitTradeScheduler.stopScheduler();
    }

    public List<ProbitBalance> getBalance() {
        return probitApiRestClient.getBalance();
    }

    public List<ProbitMarketResponse> getMarket() {
        return probitApiRestClient.getMarket();
    }

    public void getProfit() {

    }
}
