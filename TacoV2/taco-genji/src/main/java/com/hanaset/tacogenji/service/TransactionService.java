package com.hanaset.tacogenji.service;

import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderRequest;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderResponse;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.tacocommon.cache.OrderbookCached;
import com.hanaset.tacocommon.cache.upbit.UpbitTransactionCached;
import com.hanaset.tacocommon.utils.Taco2CurrencyConvert;
import com.hanaset.tacocommon.utils.TacoPercentChecker;
import com.hanaset.tacocommon.utils.UpbitStandard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class TransactionService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");

    private final UpbitApiRestClient upbitApiRestClient;
    private final UpbitBalanceService upbitBalanceService;

    public TransactionService(UpbitApiRestClient upbitApiRestClient,
                              UpbitBalanceService upbitBalanceService) {
        this.upbitApiRestClient = upbitApiRestClient;
        this.upbitBalanceService = upbitBalanceService;
    }

    @Async
    public void checkProfit(String pair) {

        if (UpbitTransactionCached.LOCK) {
            return;
        }

        try {
            UpbitOrderbookItem btcItem = OrderbookCached.UPBIT.getOrDefault("BTC-" + pair, null);
            UpbitOrderbookItem krwItem = OrderbookCached.UPBIT.getOrDefault("KRW-" + pair, null);

            if (btcItem == null || krwItem == null)
                return;

            if (TacoPercentChecker.profitCheck(Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(), UpbitStandard.PROFITPERCENT)) {

                Double amount = btcItem.getBid_size() > krwItem.getAsk_size() ? krwItem.getAsk_size() : btcItem.getBid_size();

                if (amount * btcItem.getBid_price() <= 0.0005 || amount * krwItem.getAsk_price() <= 5000) {
                    return;
                } else if (amount > UpbitTransactionCached.pairAmount.doubleValue()) {
                    amount = UpbitTransactionCached.pairAmount.doubleValue();
                }

                UpbitTransactionCached.LOCK = true;

                if (btcItem.getBid_size() > krwItem.getAsk_size()) {
                    biding(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                    asking(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);
                } else {
                    asking(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);
                    biding(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                }

                log.info("==================================================================");

                log.info("[{}] [BTC Bid : {}({})/{}] [KRW Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), BigDecimal.valueOf(btcItem.getBid_price()).toPlainString(), btcItem.getBid_size(),
                        krwItem.getAsk_price(), krwItem.getAsk_size(),
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                        (Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / krwItem.getAsk_price() * 100);

                reset("BTC");

            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), UpbitStandard.PROFITPERCENT)) {

                Double amount = krwItem.getBid_size() > btcItem.getAsk_size() ? btcItem.getAsk_size() : krwItem.getBid_size();

                if (amount * btcItem.getAsk_price() <= 0.0005 || amount * krwItem.getBid_price() <= 5000) {
                    return;
                } else if (amount > UpbitTransactionCached.pairAmount.doubleValue()) {
                    amount = UpbitTransactionCached.pairAmount.doubleValue();
                }

                if (krwItem.getBid_size() > btcItem.getAsk_size()) {
                    biding(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);
                    asking(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                } else {
                    asking(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                    biding(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);
                }

                log.info("==================================================================");

                log.info("[{}] [KRW Bid : {}/{}] [BTC Ask : {}({})/{}] [profit : {}] [percent : {}]",
                        pair,
                        krwItem.getBid_price(), krwItem.getBid_size(),
                        Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), BigDecimal.valueOf(btcItem.getAsk_price()).toPlainString(), btcItem.getAsk_size(),
                        krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()),
                        (krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price())) / Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()) * 100);

                reset("KRW");

            }

        } catch (Exception e) {
            log.error("[{}] Upbit Data error -> {}", pair, e.getMessage());
            UpbitTransactionCached.LOCK = false;
        }
    }


    @Async
    public Response<UpbitOrderResponse> biding(UpbitOrderbookItem askitem, BigDecimal amount, String pair) throws IOException {

        // 매수
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("bid")
                .price(BigDecimal.valueOf(askitem.getAsk_price()).toPlainString())
                .volume(amount.toPlainString())
                .ord_type("limit")
                .build();

        Response<UpbitOrderResponse> response = upbitApiRestClient.createOrder(request).execute();

        if (response.isSuccessful()) {
            log.info("매수:{}", response.body().toString());
        } else {
            log.error("매수 오류:{}", response.errorBody().byteString().toString());
        }

        return response;

    }

    @Async
    public Response<UpbitOrderResponse> asking(UpbitOrderbookItem biditem, BigDecimal amount, String pair) throws IOException {

        // 매도
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("ask")
                .price(BigDecimal.valueOf(biditem.getBid_price()).toPlainString())
                .volume(amount.toPlainString())
                .ord_type("limit")
                .build();

        Response<UpbitOrderResponse> response = upbitApiRestClient.createOrder(request).execute();

        if (response.isSuccessful()) {
            log.info("매도:{}", response.body().toString());
        } else {
            log.error("매도 오류:{}", response.errorBody().byteString().toString());
        }

        return response;
    }

    public Response<UpbitOrderResponse> orderDeleting(String uuid) throws IOException {

        return upbitApiRestClient.deleteOrder(uuid).execute();
    }

    private void reset(String type) {

        try {
            System.out.println("Sleep before");
            Thread.sleep(1000 * 3);
        } catch (InterruptedException e) {
            log.error("reset Sleep error");
        }

        //upbitBalanceService.exchangeResult(type);
        UpbitTransactionCached.LOCK = false;
        System.out.println("Sleep after");
    }

}
