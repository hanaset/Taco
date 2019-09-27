package com.hanaset.taco.service.upbit;

import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.api.upbit.model.UpbitOrderResponse;
import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.taco.api.upbit.model.UpbitTicket;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.cache.UpbitTransactionCached;
import com.hanaset.taco.utils.Taco2CurrencyConvert;
import com.hanaset.taco.utils.TacoPercentChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@SuppressWarnings("Duplicates")
public class UpbitSpecificTransactionService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");

    private final UpbitApiRestClient upbitApiRestClient;
    private final UpbitBalanceService upbitBalanceService;
    private final Double profit = 0.4;

    public UpbitSpecificTransactionService(UpbitApiRestClient upbitApiRestClient,
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

            if (TacoPercentChecker.profitCheck(Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(), profit)) {

                Double base_amount = btcItem.getBid_size() > krwItem.getAsk_size() ? krwItem.getAsk_size() : btcItem.getBid_size();
                Double amount = base_amount / 10.f;

                if (amount * btcItem.getBid_price() <= 0.0005 || amount * krwItem.getAsk_price() <= 5000) {
                    return;
                }

                UpbitTransactionCached.LOCK = true;

                log.info("==================================================================");

                log.info("[{}] [BTC Bid : {}({})/{}] [KRW Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), BigDecimal.valueOf(btcItem.getBid_price()).toPlainString(), btcItem.getBid_size(),
                        krwItem.getAsk_price(), krwItem.getAsk_size(),
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                        (Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / krwItem.getAsk_price() * 100);

                Response<UpbitOrderResponse> askResponse = asking(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);
                Response<UpbitOrderResponse> bidResponse = biding(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);

                reset(pair);

            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), profit)) {

                Double base_amount = krwItem.getBid_size() > btcItem.getAsk_size() ? btcItem.getAsk_size() : krwItem.getBid_size();
                Double amount = base_amount / 10.f;

                if (amount * btcItem.getAsk_price() <= 0.0005 || amount * krwItem.getBid_price() <= 5000) {
                    return;
                }

                UpbitTransactionCached.LOCK = true;

                log.info("==================================================================");

                log.info("[{}] [KRW Bid : {}/{}] [BTC Ask : {}({})/{}] [profit : {}] [percent : {}]",
                        pair,
                        krwItem.getBid_price(), krwItem.getBid_size(),
                        Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), BigDecimal.valueOf(btcItem.getAsk_price()).toPlainString(), btcItem.getAsk_size(),
                        krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()),
                        (krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price())) / Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()) * 100);

                Response<UpbitOrderResponse> askResponse = asking(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                Response<UpbitOrderResponse> bidResponse = biding(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);

                reset(pair);


            }

        } catch (Exception e) {
            log.error("[{}] Upbit Data error -> {}", pair, e.getMessage());
            UpbitTransactionCached.LOCK = false;
        }
    }


    private Response<UpbitOrderResponse> biding(UpbitOrderbookItem askitem, BigDecimal amount, String pair) throws IOException {

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

    private Response<UpbitOrderResponse> asking(UpbitOrderbookItem biditem, BigDecimal amount, String pair) throws IOException {

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

    private void reset(String pair) {

        try {
            System.out.println("Sleep before");
            Thread.sleep(1000 * 3);
        } catch (InterruptedException e) {
            log.error("reset Sleep error");
        }

        exchangeProfit();
        System.out.println("Sleep after");
    }

    public void exchangeProfit() {

        BigDecimal myBalance = upbitBalanceService.getUpbitMarketAccount("BTC");

        UpbitOrderbookItem converItem = new UpbitOrderbookItem();
        converItem.setAsk_price(OrderbookCached.UPBIT_BTC.get("ask").doubleValue());
        converItem.setBid_price(OrderbookCached.UPBIT_BTC.get("bid").doubleValue());

        if (myBalance.compareTo(BigDecimal.valueOf(0.005)) == 1) {

            try {
                Response<UpbitOrderResponse> exchangeResponse = asking(converItem, myBalance.subtract(BigDecimal.valueOf(0.01)), "KRW-BTC");

            } catch (IOException e) {
                log.error("환전 에러:{}", e.getMessage());
            }
        } else if (myBalance.compareTo(BigDecimal.valueOf(0.005)) == -1) {

            try {
                Response<UpbitOrderResponse> exchangeResponse = biding(converItem, BigDecimal.valueOf(0.01).subtract(myBalance), "KRW-BTC");

            } catch (IOException e) {
                log.error("환전 에러:{}", e.getMessage());
            }
        }

    }
}
