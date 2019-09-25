package com.hanaset.taco.service.upbit;

import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.*;
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
public class UpbitMarketTransactionService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");

    private final UpbitApiRestClient upbitApiRestClient;
    private final UpbitBalanceService upbitBalanceService;
    private final Double profit = 0.4;

    public UpbitMarketTransactionService(UpbitApiRestClient upbitApiRestClient,
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

                if (amount * btcItem.getBid_price() <= 0.0005 || amount * krwItem.getAsk_price() <= 10000) {
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

                //Response<UpbitOrderResponse> bidResponse = bidingMarket(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);
                Response<UpbitOrderResponse> bidResponse = biding(krwItem, BigDecimal.valueOf(amount), "KRW-" + pair);

                if (bidResponse.isSuccessful()) {
                    log.info("매수:{}", bidResponse.body().toString());

                    UpbitTicket ticket = UpbitTicket.builder()
                            .uuid(bidResponse.body().getUuid())
                            .market(pair)
                            .bid_market(bidResponse.body().getMarket())
                            .ask_market("BTC-" + pair)
                            .bidOrderbookItem(krwItem)
                            .askOrderbookItem(btcItem)
                            .amount(BigDecimal.valueOf(amount))
                            .ask_amount(BigDecimal.valueOf(krwItem.getAsk_size()))
                            .bid_amount(BigDecimal.valueOf(btcItem.getBid_size()))
                            .build();

                    UpbitTransactionCached.TICKET = ticket;

                } else {
                    log.error("매수 오류:{}", bidResponse.errorBody().byteString().toString());
                    UpbitTransactionCached.LOCK = false;
                }

            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), profit)) {

                Double base_amount = krwItem.getBid_size() > btcItem.getAsk_size() ? btcItem.getAsk_size() : krwItem.getBid_size();
                Double amount = base_amount / 10.f;

                if (amount * btcItem.getAsk_price() <= 0.0005 || amount * krwItem.getBid_price() <= 10000) {
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


                Response<UpbitOrderResponse> bidResponse = biding(btcItem, BigDecimal.valueOf(amount), "BTC-" + pair);


                if (bidResponse.isSuccessful()) {
                    log.info("매수:{}", bidResponse.body().toString());

                    UpbitTicket ticket = UpbitTicket.builder()
                            .uuid(bidResponse.body().getUuid())
                            .market(pair)
                            .bid_market(bidResponse.body().getMarket())
                            .ask_market("KRW-" + pair)
                            .bidOrderbookItem(btcItem)
                            .askOrderbookItem(krwItem)
                            .amount(BigDecimal.valueOf(amount))
                            .ask_amount(BigDecimal.valueOf(btcItem.getAsk_size()))
                            .bid_amount(BigDecimal.valueOf(krwItem.getBid_size()))
                            .build();

                    UpbitTransactionCached.TICKET = ticket;

                } else {
                    log.error("매수 오류:{}", bidResponse.errorBody().byteString().toString());
                    UpbitTransactionCached.LOCK = false;
                }
            }

        } catch (Exception e) {
            log.error("[{}] Upbit Data error -> {}", pair, e.getMessage());
            UpbitTransactionCached.LOCK = false;
        }
    }

    public void orderProfit(UpbitTrade upbitTrade) {

        if (UpbitTransactionCached.TICKET == null || upbitTrade == null)
            return;

        if (UpbitTransactionCached.LOCK == false) { // 거래 요청 후 락일 경우에만 처리
            return;
        }

        if (UpbitTransactionCached.TICKET.getBid_market().equals(upbitTrade.getCode()) &&
                upbitTrade.getAsk_bid().equals("BID")) {
            bidProfit(upbitTrade);
        } else if (UpbitTransactionCached.TICKET.getAsk_market().equals(upbitTrade.getCode()) &&
                upbitTrade.getAsk_bid().equals("ASK")) {
            askProfit(upbitTrade);
        }

    }

    @Async
    public void bidProfit(UpbitTrade upbitTrade) {

        UpbitTicket ticket = UpbitTransactionCached.TICKET;

        if (upbitTrade.getTrade_price().compareTo(BigDecimal.valueOf(ticket.getBidOrderbookItem().getAsk_price())) != 0)
            return;

        System.out.println(upbitTrade);

        BigDecimal myBalance = upbitBalanceService.getUpbitMarketAccount(ticket.getMarket());

        try {
            Response<UpbitOrderResponse> askResponse, balanceAskResponse;

//            if (ticket.getAsk_market().contains("KRW")) {
//                askResponse = askingMarket(ticket.getAskOrderbookItem(), ticket.getAmount(), ticket.getAsk_market());
//                balanceAskResponse = askingMarket(ticket.getAskOrderbookItem(), myBalance, ticket.getAsk_market());
//            } else {
                askResponse = asking(ticket.getAskOrderbookItem(), ticket.getAmount(), ticket.getAsk_market());
                balanceAskResponse = asking(ticket.getAskOrderbookItem(), myBalance, ticket.getAsk_market());
//            }

            if (askResponse.isSuccessful()) {
                log.info("매도:{}", askResponse.body());
                UpbitTransactionCached.TICKET.setUuid(askResponse.body().getUuid());
                reset(ticket.getMarket());
            } else if (balanceAskResponse.isSuccessful()) {
                log.info("매도:{}", balanceAskResponse.body());
                UpbitTransactionCached.TICKET.setUuid(balanceAskResponse.body().getUuid());
                reset(ticket.getMarket());
            } else {
                log.error("매도 에러: {}/{}", askResponse.errorBody().byteString().toString(), balanceAskResponse.errorBody().byteString().toString());
                UpbitTransactionCached.COUNT++;
            }
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
        }

        if (UpbitTransactionCached.COUNT >= 3) {

            try {
                Response<UpbitOrderResponse> deleteResponse = orderDeleting(ticket.getUuid());

                if (deleteResponse.isSuccessful()) {
                    log.info("매수 취소: {}", deleteResponse.body().toString());
                } else {
                    log.error("매수 취소 에러 :{}", deleteResponse.errorBody().byteString().toString());
                }
            } catch (IOException e) {
                log.error("매수 취소 IOException: {}", e.getMessage());
            }
            reset(ticket.getMarket());
        }
    }

    public void exchangeProfit() {

        BigDecimal myBalance = upbitBalanceService.getUpbitMarketAccount("BTC");

        UpbitOrderbookItem converItem = new UpbitOrderbookItem();
        converItem.setAsk_price(OrderbookCached.UPBIT_BTC.get("ask").doubleValue());
        converItem.setBid_price(OrderbookCached.UPBIT_BTC.get("bid").doubleValue());

        if (myBalance.compareTo(BigDecimal.valueOf(0.01)) == 1) {

            try {
                Response<UpbitOrderResponse> exchangeResponse = asking(converItem, myBalance.subtract(BigDecimal.valueOf(0.01)), "KRW-BTC");

                if (exchangeResponse.isSuccessful()) {
                    log.info("환전:{}", exchangeResponse.body().toString());
                } else {
                    log.error("환전 실패:{}", exchangeResponse.errorBody().byteString().toString());
                }

            } catch (IOException e) {
                log.error("환전 에러:{}", e.getMessage());
            }
        } else if (myBalance.compareTo(BigDecimal.valueOf(0.01)) == -1) {

            try {
                Response<UpbitOrderResponse> exchangeResponse = biding(converItem, BigDecimal.valueOf(0.01).subtract(myBalance), "KRW-BTC");

                if (exchangeResponse.isSuccessful()) {
                    log.info("환전:{}", exchangeResponse.body().toString());
                } else {
                    log.error("환전 실패:{}", exchangeResponse.errorBody().byteString().toString());
                }
            } catch (IOException e) {
                log.error("환전 에러:{}", e.getMessage());
            }
        }

    }

    @Async
    public void askProfit(UpbitTrade upbitTrade) {

        UpbitTicket ticket = UpbitTransactionCached.TICKET;

        if (upbitTrade.getTrade_price().compareTo(BigDecimal.valueOf(ticket.getAskOrderbookItem().getBid_price())) != 0)
            return;

        System.out.println(upbitTrade);

        BigDecimal myBalance = upbitBalanceService.getUpbitMarketAccount(ticket.getMarket());

        try {

            Response<UpbitOrderResponse> askResponse, balanceAskResponse;


//            if (ticket.getAsk_market().contains("KRW")) {
//                askResponse = askingMarket(ticket.getAskOrderbookItem(), ticket.getAmount(), ticket.getAsk_market());
//                balanceAskResponse = askingMarket(ticket.getAskOrderbookItem(), myBalance, ticket.getAsk_market());
//            } else {
                askResponse = asking(ticket.getAskOrderbookItem(), ticket.getAmount(), ticket.getAsk_market());
                balanceAskResponse = asking(ticket.getAskOrderbookItem(), myBalance, ticket.getAsk_market());
//            }


            if (askResponse.isSuccessful()) {
                log.info("매도:{}", askResponse.body());
                UpbitTransactionCached.TICKET.setUuid(askResponse.body().getUuid());
                reset(ticket.getMarket());
            } else if (balanceAskResponse.isSuccessful()) {
                log.info("매도:{}", balanceAskResponse.body());
                UpbitTransactionCached.TICKET.setUuid(balanceAskResponse.body().getUuid());
                reset(ticket.getMarket());
            } else {
                log.error("매도 에러: {}/{}", askResponse.errorBody().byteString().toString(), balanceAskResponse.errorBody().byteString().toString());
                UpbitTransactionCached.COUNT++;
            }
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
        }

        if (UpbitTransactionCached.COUNT >= 3) {

            try {
                Response<UpbitOrderResponse> deleteResponse = orderDeleting(ticket.getUuid());

                if (deleteResponse.isSuccessful()) {
                    log.info("매수 취소: {}", deleteResponse.body().toString());
                } else {
                    log.error("매수 취소 에러 :{}", deleteResponse.errorBody().byteString().toString());
                }
            } catch (IOException e) {
                log.error("매수 취소 IOException: {}", e.getMessage());
            }
            //UpbitTransactionCached.reset();
            reset(ticket.getMarket());
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

        System.out.println(request);

        return upbitApiRestClient.createOrder(request).execute();
    }

    public Response<UpbitOrderResponse> orderDeleting(String uuid) throws IOException {

        return upbitApiRestClient.deleteOrder(uuid).execute();
    }

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

        System.out.println(request);

        return upbitApiRestClient.askOrder(request).execute();
    }

    private void reset(String pair) {

        try {
            System.out.println("Sleep before");
            Thread.sleep(1000 * 5);
            System.out.println("매수 취소");
            orderDeleting(UpbitTransactionCached.TICKET.getUuid());
            BigDecimal myBalance = upbitBalanceService.getUpbitMarketAccount(pair);
            Response<UpbitOrderResponse> askResponse = askingMarket(null, myBalance, "KRW-" + pair);

            if (askResponse.isSuccessful()) {
                log.info("잔액 처리 성공");
            } else {
                //log.info("정상 처리 성공");
            }
        } catch (IOException e) {
            log.error("reset error");
        } catch (InterruptedException e) {
            log.error("reset Sleep error");
        }

        exchangeProfit();
        System.out.println("Sleep after");
        UpbitTransactionCached.reset();
    }
}
