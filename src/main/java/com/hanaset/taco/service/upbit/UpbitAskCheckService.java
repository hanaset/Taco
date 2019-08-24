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
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
public class UpbitAskCheckService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UpbitApiRestClient upbitApiRestClient;

    public UpbitAskCheckService(TransactionHistoryRepository transactionHistoryRepository,
                                UpbitApiRestClient upbitApiRestClient) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.upbitApiRestClient = upbitApiRestClient;
    }

    public void compareASKWithBID(String pair) {

        try {
            UpbitOrderbookItem btcItem = OrderbookCached.UPBIT.get("BTC-" + pair);
            UpbitOrderbookItem krwItem = OrderbookCached.UPBIT.get("KRW-" + pair);

            if (TacoPercentChecker.profitCheck(Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(), 0.35)) {

                Double amount = btcItem.getBid_size() > krwItem.getAsk_size() ? krwItem.getAsk_size() : btcItem.getBid_size();
                amount /= 10.f;

                TransactionHistoryEntity entity = TransactionHistoryEntity.builder()
                        .askPair("KRW-" + pair)
                        .bidPair("BTC-" + pair)
                        .askAmount(amount)
                        .bidAmount(amount)
                        .nowBTC(OrderbookCached.UPBIT_BTC.doubleValue())
                        .build();

                log.info("[{}] [BTC Bid : {}/{}] [KRW Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()), btcItem.getBid_size(),
                        krwItem.getAsk_price(), krwItem.getAsk_size(),
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                        (Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / krwItem.getAsk_price() * 100);

                try {
                    Response<UpbitOrderResponse> bidResponse = bidingKRW(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);

                    Thread.sleep(200);

                    if (bidResponse.isSuccessful()) {

                        entity.setBidPrice(bidResponse.body().getPrice()/amount);
                        entity.setBidSnapShot(ZonedDateTime.now());
                        System.out.println(bidResponse.body());

                        Response<UpbitOrderResponse> askResponse = askingBTC(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);
                        if (!askResponse.isSuccessful()) {
                            log.error("BTC ASK error -> {}",askResponse.errorBody().byteString().toString());
                            orderDeleting(bidResponse.body().getUuid());
                        } else {
                            System.out.println(askResponse.body());
                            entity.setAskPrice(askResponse.body().getPrice() / amount);
                            entity.setAskSnapShot(ZonedDateTime.now());

                            transactionHistoryRepository.save(entity);
                        }
                    } else {
                        log.error(bidResponse.errorBody().byteString().toString());
                    }
                } catch (IOException e) {
                    log.error("KRW BID error -> {}", e.getMessage());
                } catch (InterruptedException e){
                    log.error("KRW BID slepp error");
                }


            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()), 0.35)) {

                Double amount = krwItem.getBid_size() > btcItem.getAsk_size() ? btcItem.getAsk_size() : krwItem.getBid_size();
                amount /= 10.f;

                log.info("[{}] [KRW Bid : {}/{}] [BTC Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        krwItem.getBid_price(), krwItem.getBid_size(),
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()), btcItem.getAsk_size(),
                        krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()),
                        (krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price())) / Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()) * 100);

                TransactionHistoryEntity entity = TransactionHistoryEntity.builder()
                        .askPair("BTC-" + pair)
                        .bidPair("KRW-" + pair)
                        .askAmount(amount)
                        .bidAmount(amount)
                        .nowBTC(OrderbookCached.UPBIT_BTC.doubleValue())
                        .build();
                try {
                    Response<UpbitOrderResponse> bidResponse = bidingBTC(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);

                    Thread.sleep(200);

                    if (bidResponse.isSuccessful()) {

                        entity.setBidPrice(bidResponse.body().getPrice()/amount);
                        entity.setBidSnapShot(ZonedDateTime.now());
                        System.out.println(bidResponse.body());

                        Response<UpbitOrderResponse> askResponse = askingKRW(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                        if (!askResponse.isSuccessful()) {
                            log.error("BTC ASK error -> {}",askResponse.errorBody().byteString().toString());
                            orderDeleting(bidResponse.body().getUuid());
                        } else {
                            System.out.println(askResponse.body());
                            entity.setAskPrice(askResponse.body().getPrice() / amount);
                            entity.setAskSnapShot(ZonedDateTime.now());

                            transactionHistoryRepository.save(entity);
                        }
                    } else {
                        log.error(bidResponse.errorBody().byteString().toString());
                    }
                } catch (IOException e) {
                    log.error("BTC BID error -> {}", e.getMessage());
                } catch (InterruptedException e) {
                    log.error("BTC BID sleep error");
                }
            }

        } catch (NullPointerException e) {
            log.error("[{}] Upbit Data Null -> {}", pair, e.getMessage());
        }
    }

    private Response<UpbitOrderResponse> bidingKRW(UpbitOrderbookItem askitem, BigDecimal amount, String pair) throws IOException {

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


    private Response<UpbitOrderResponse> askingKRW(UpbitOrderbookItem biditem, BigDecimal amount, String pair) throws IOException {

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

    private Response<UpbitOrderResponse> bidingBTC(UpbitOrderbookItem askitem, BigDecimal amount, String pair) throws IOException {

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


    private Response<UpbitOrderResponse> askingBTC(UpbitOrderbookItem biditem, BigDecimal amount, String pair) throws IOException {

        // 매도
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("ask")
                .price(BigDecimal.valueOf(biditem.getAsk_price()).toPlainString())
                .volume(amount.toPlainString())
                .ord_type("limit")
                .build();

        return upbitApiRestClient.createOrder(request).execute();
    }

    private Response<UpbitOrderResponse> orderDeleting(String uuid) throws IOException {

        return upbitApiRestClient.deleteOrder(uuid).execute();
    }
}
