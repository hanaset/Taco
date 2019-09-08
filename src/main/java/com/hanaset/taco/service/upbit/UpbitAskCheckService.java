package com.hanaset.taco.service.upbit;

import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.api.upbit.model.UpbitOrderResponse;
import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.entity.TransactionHistoryEntity;
import com.hanaset.taco.repository.TransactionHistoryRepository;
import com.hanaset.taco.utils.Taco2CurrencyConvert;
import com.hanaset.taco.utils.TacoPercentChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@SuppressWarnings("Duplicates")
public class UpbitAskCheckService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UpbitApiRestClient upbitApiRestClient;

    private Integer gap = 400;

    public UpbitAskCheckService(TransactionHistoryRepository transactionHistoryRepository,
                                UpbitApiRestClient upbitApiRestClient) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.upbitApiRestClient = upbitApiRestClient;
    }

    @Async
    public void compareASKWithBID(String pair){

        if(OrderbookCached.UPBIT_LOCK){
            return;
        }

        try {
            UpbitOrderbookItem btcItem = OrderbookCached.UPBIT.get("BTC-" + pair);
            UpbitOrderbookItem krwItem = OrderbookCached.UPBIT.get("KRW-" + pair);

            UpbitOrderbookItem converItem = new UpbitOrderbookItem();

            if (TacoPercentChecker.profitCheck(Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(), 0.37)) {

                Double base_amount = btcItem.getBid_size() > krwItem.getAsk_size() ? krwItem.getAsk_size() : btcItem.getBid_size();
                Double amount = base_amount / 5.f;

                if( amount * btcItem.getBid_price() <= 0.0005 || amount * krwItem.getAsk_price() <= 5000) {
//                    amount += (base_amount / 10.f);
//                    if(base_amount < amount)
                    return;
                }

                OrderbookCached.lock_chage(true);

                log.info("[{}] [BTC Bid : {}({})/{}] [KRW Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), BigDecimal.valueOf(btcItem.getBid_price()).toPlainString(),btcItem.getBid_size(),
                        krwItem.getAsk_price(), krwItem.getAsk_size(),
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                        (Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / krwItem.getAsk_price() * 100);

                TransactionHistoryEntity entity = TransactionHistoryEntity.builder()
                        .market("upbit")
                        .askPair("KRW-" + pair)
                        .bidPair("BTC-" + pair)
                        .askAmount(amount)
                        .bidAmount(amount)
                        .nowBTC(OrderbookCached.UPBIT_BTC.get("bid").doubleValue())
                        .build();

                try {
                    Response<UpbitOrderResponse> bidResponse = biding(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);

                    Thread.sleep(gap);

                    if (bidResponse.isSuccessful()) {

                        entity.setBidPrice(bidResponse.body().getPrice());
                        entity.setBidSnapShot(ZonedDateTime.now());
                        log.info("매수:{}", bidResponse.body().toString());

                        Response<UpbitOrderResponse> askResponse = asking(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);
                        if (!askResponse.isSuccessful()) {
                            log.error("매도 에러:{}",askResponse.errorBody().byteString().toString());
                            log.info("매수 취소:{}", orderDeleting(bidResponse.body().getUuid()).body().toString());
                        } else {
                            log.info("매도:{}", askResponse.body().toString());
                            entity.setAskPrice(askResponse.body().getPrice());
                            entity.setAskSnapShot(ZonedDateTime.now());

                            transactionHistoryRepository.save(entity);

                            // 원화 -> 비트코인으로 변한 만큼 다시 비트코인 판매
                            Thread.sleep(gap);
                            converItem.setBid_price(OrderbookCached.UPBIT_BTC.get("bid").doubleValue());
                            log.info("환전:{}", askingMarket(converItem, BigDecimal.valueOf(amount * btcItem.getAsk_price()), "KRW-BTC").body().toString());

                            Thread.sleep(gap * 10);
                        }
                    } else {
                        log.error("매수 에러:{}", bidResponse.errorBody().byteString().toString());
                    }
                } catch (IOException e) {
                    log.error("KRW BID / BTC ASK error -> {}", e.getMessage());
                } catch (InterruptedException e){
                    log.error("sleep error");
                }

            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), 0.37)) {

                Double base_amount = krwItem.getBid_size() > btcItem.getAsk_size() ? btcItem.getAsk_size() : krwItem.getBid_size();
                Double amount = base_amount / 5.f;

                if( amount * btcItem.getAsk_price() <= 0.0005 || amount * krwItem.getBid_price() <= 5000) {
//                    amount += (base_amount / 10.f);
//                    if(base_amount < amount)
                    return;
                }

                OrderbookCached.lock_chage(true);

                log.info("[{}] [KRW Bid : {}/{}] [BTC Ask : {}({})/{}] [profit : {}] [percent : {}]",
                        pair,
                        krwItem.getBid_price(), krwItem.getBid_size(),
                        Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), BigDecimal.valueOf(btcItem.getAsk_price()).toPlainString(), btcItem.getAsk_size(),
                        krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()),
                        (krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price())) / Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()) * 100);

                TransactionHistoryEntity entity = TransactionHistoryEntity.builder()
                        .market("upbit")
                        .askPair("BTC-" + pair)
                        .bidPair("KRW-" + pair)
                        .askAmount(amount)
                        .bidAmount(amount)
                        .nowBTC(OrderbookCached.UPBIT_BTC.get("ask").doubleValue())
                        .build();
                try {
                    Response<UpbitOrderResponse> bidResponse = biding(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);

                    Thread.sleep(gap);

                    if (bidResponse.isSuccessful()) {

                        entity.setBidPrice(bidResponse.body().getPrice());
                        entity.setBidSnapShot(ZonedDateTime.now());
                        log.info("매수:{}", bidResponse.body().toString());
                        Response<UpbitOrderResponse> askResponse = asking(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                        if (!askResponse.isSuccessful()) {
                            log.error("매도 오류:{}",askResponse.errorBody().byteString().toString());
                            log.info("매수 취소:{}", orderDeleting(bidResponse.body().getUuid()).body().toString());
                        } else {
                            log.info("매도:{}", askResponse.body().toString());
                            entity.setAskPrice(askResponse.body().getPrice());
                            entity.setAskSnapShot(ZonedDateTime.now());

                            transactionHistoryRepository.save(entity);

                            Thread.sleep(gap);
                            converItem.setAsk_price(OrderbookCached.UPBIT_BTC.get("ask").doubleValue());
                            log.info("환전:{}", bidingMarket(converItem, BigDecimal.valueOf(amount * btcItem.getBid_price()), "KRW-BTC").body().toString());

                            Thread.sleep(gap * 10);
                        }
                    } else {
                        log.error("매수 오류:{}", bidResponse.errorBody().byteString().toString());
                    }
                } catch (IOException e) {
                    log.error("BTC BID / KRW ASK error -> {}", e.getMessage());
                } catch (InterruptedException e){
                    log.error("sleep error");
                }

            }

        } catch (NullPointerException e) {
            log.error("[{}] Upbit Data Null -> {}", pair, e.getMessage());
        } finally {
            OrderbookCached.lock_chage(false);
            //Thread.sleep(5000);
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

        return upbitApiRestClient.createOrder(request).execute();

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

        return upbitApiRestClient.createOrder(request).execute();
    }

    private Response<UpbitOrderResponse> orderDeleting(String uuid) throws IOException {

        return upbitApiRestClient.deleteOrder(uuid).execute();
    }

    /// 시장가

    private Response<UpbitOrderResponse> bidingMarket(UpbitOrderbookItem askitem, BigDecimal amount, String pair) throws IOException {

        // 매수
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("bid")
                .price(amount.multiply(BigDecimal.valueOf(askitem.getAsk_price())).toPlainString())
                .volume(null)
                .ord_type("price")
                .build();

        return upbitApiRestClient.bidOrder(request).execute();
    }


    private Response<UpbitOrderResponse> askingMarket(UpbitOrderbookItem biditem, BigDecimal amount, String pair) throws IOException {

        // 매도
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("ask")
                .price(null)
                .volume(amount.toPlainString())
                .ord_type("market")
                .build();

        return upbitApiRestClient.askOrder(request).execute();
    }

}
