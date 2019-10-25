package com.hanaset.tacoreaper.service.okex;

import com.hanaset.tacocommon.api.okex.OkexApiRestClient;
import com.hanaset.tacocommon.api.okex.model.OkexAccount;
import com.hanaset.tacocommon.utils.PairUtils;
import com.hanaset.tacoreaper.api.upbit.UpbitReaperWebSocketService;
import com.hanaset.tacoreaper.scheduler.okex.ReaperOkexTradeScheduler;
import com.hanaset.tacoreaper.scheduler.probit.ReaperProbitTradeScheduler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReaperOkexService {

    private final OkexApiRestClient okexApiRestClient;
    private final UpbitReaperWebSocketService upbitReaperWebSocketService;
    private final ReaperOkexTradeService reaperOkexTradeService;
    private ReaperOkexTradeScheduler reaperOkexTradeScheduler;

    public ReaperOkexService(OkexApiRestClient okexApiRestClient,
                             UpbitReaperWebSocketService upbitReaperWebSocketService,
                             ReaperOkexTradeService reaperOkexTradeService) {
        this.okexApiRestClient = okexApiRestClient;
        this.upbitReaperWebSocketService = upbitReaperWebSocketService;
        this.reaperOkexTradeService = reaperOkexTradeService;
    }

    public List<OkexAccount> getAccount() {
       // return okexApiRestClient.getAccount();
        return okexApiRestClient.getSpotAccount();
    }


    public void serviceStart(String pair) {
        upbitReaperWebSocketService.conncentTrade(pair);
        reaperOkexTradeScheduler = new ReaperOkexTradeScheduler(reaperOkexTradeService);
        reaperOkexTradeScheduler.startScheduler(PairUtils.getPair(pair));

    }

    public void serviceFinish() {
        upbitReaperWebSocketService.disconncetTrade();
        reaperOkexTradeScheduler.stopScheduler();
    }
}
