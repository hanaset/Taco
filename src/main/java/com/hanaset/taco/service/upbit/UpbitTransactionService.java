package com.hanaset.taco.service.upbit;

import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.*;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.cache.UpbitTransactionCached;
import com.hanaset.taco.utils.Taco2CurrencyConvert;
import com.hanaset.taco.utils.TacoPercentChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@SuppressWarnings("Duplicates")
public class UpbitTransactionService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");

    private final UpbitApiRestClient upbitApiRestClient;
    private final UpbitBalanceService upbitBalanceService;
    private final Double profit = 0.35;
    private final int DELAY = 3000;

    public UpbitTransactionService(UpbitApiRestClient upbitApiRestClient,
                                   UpbitBalanceService upbitBalanceService) {
        this.upbitApiRestClient = upbitApiRestClient;
        this.upbitBalanceService = upbitBalanceService;
    }

    public void checkProfit(String pair) {

        if (UpbitTransactionCached.LOCK) {
            return;
        }

        try {
            UpbitOrderbookItem btcItem = OrderbookCached.UPBIT.get("BTC-" + pair);
            UpbitOrderbookItem krwItem = OrderbookCached.UPBIT.get("KRW-" + pair);

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

        if (!UpbitTransactionCached.TICKET.getBid_market().equals(upbitTrade.getCode()))
            return;

        if (upbitTrade.getAsk_bid().equals("BID")) {
            bidProfit(upbitTrade);
        } else if (upbitTrade.getAsk_bid().equals("ASK")) {
            askProfit(upbitTrade);
        }

    }

    private void bidProfit(UpbitTrade upbitTrade) {

        UpbitTicket ticket = UpbitTransactionCached.TICKET;

        if (upbitTrade.getTrade_price().compareTo(BigDecimal.valueOf(ticket.getBidOrderbookItem().getAsk_price())) != 0)
            return;

        BigDecimal myBalance = upbitBalanceService.getUpbitMarketAccount(ticket.getMarket());

        if (myBalance.compareTo(BigDecimal.ZERO) == 0) { // 내 코인이 안사졌을 경우

            BigDecimal enableAmount = ticket.getAsk_amount().subtract(upbitTrade.getTrade_volume());

            if (enableAmount.compareTo(ticket.getAmount()) < 0) { // 구매 취소
                // 구매 가능 수량 - 구매가 일어난 수량 >= 내가 구매할 수량 (즉시 구매)
                // 구매 가능 수량 - 구매가 일어난 수량 < 내가 구매할 수량 (대기) -> 구매 취소

                try {
                    Response<UpbitOrderResponse> deleteResponse = orderDeleting(ticket.getUuid());

                    if (deleteResponse.isSuccessful()) {
                        log.info("매수 취소: {}", deleteResponse.body().toString());
                    } else {
                        log.error("매수 취소 에러 :{}", deleteResponse.errorBody().byteString().toString());
                    }
                } catch (IOException e) {
                    log.error("매수 취소 에러: {}", e.getMessage());
                }
                UpbitTransactionCached.TICKET = null;
                UpbitTransactionCached.LOCK = false;
            } else {
                ticket.setAsk_amount(enableAmount);
                // 구매 가능한 수량이 남아 있기에 남은 수량을 담에 비교 할 수 있도록 저장
            }
            return;
        }

        try {
            Response<UpbitOrderResponse> askResponse = asking(ticket.getAskOrderbookItem(), myBalance, ticket.getAsk_market());

            if (askResponse.isSuccessful()) {
                log.info("매도:{}", askResponse.body());

            } else {
                log.error("매도 에러: {}", askResponse.errorBody().byteString().toString());
            }
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
        }
    }

    private void exchangeProfit() {

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

    private void askProfit(UpbitTrade upbitTrade) {

        UpbitTicket ticket = UpbitTransactionCached.TICKET;

        if (upbitTrade.getTrade_price().compareTo(BigDecimal.valueOf(ticket.getAskOrderbookItem().getBid_price())) != 0)
            return;

        BigDecimal myBalance = upbitBalanceService.getUpbitMarketAccount(ticket.getMarket());
        BigDecimal enableAmount = BigDecimal.valueOf(ticket.getAskOrderbookItem().getBid_size()).subtract(ticket.getAmount());

        if (myBalance.compareTo(BigDecimal.ZERO) != 0) { // 내 코인이 안팔렸을 경우

            if (enableAmount.compareTo(ticket.getAmount()) < 0) { // 구매 취소
                // 판매 가능 수량 - 판매가 일어난 수량 >= 내가 판매할 수량 (즉시 판매)
                // 판매 가능 수량 - 판매가 일어난 수량 < 내가 판매할 수량 (대기) -> 구매 취소

                try {
                    Response<UpbitOrderResponse> deleteResponse = orderDeleting(ticket.getUuid());

                    if (deleteResponse.isSuccessful()) {
                        log.info("매수 취소: {}", deleteResponse.body().toString());
                    } else {
                        log.error("매수 취소 에러 :{}", deleteResponse.errorBody().byteString().toString());
                    }
                } catch (IOException e) {
                    log.error("매수 취소 에러: {}", e.getMessage());
                }
                UpbitTransactionCached.TICKET = null;
                UpbitTransactionCached.LOCK = false;

            } else {
                // 판매 가능 수량이 남아 있을 경우 담에 다시 비교 할 수 있도록 저장
                ticket.setBid_amount(enableAmount);
            }

            return;

        } else { // 내 지갑에 코인이 없다?? 무조건 취소 한번하고 시작

            if(enableAmount.compareTo(ticket.getAmount()) < 0) {
                // 판매 가능 수량 - 판매가 일어난 수량 < 내가 판매할 수량 (대기) -> 구매 취소 // 어짜피 이미 구매했으면 무시되는 코드
                try {
                    Response<UpbitOrderResponse> deleteResponse = orderDeleting(ticket.getUuid());

                    if (deleteResponse.isSuccessful()) {
                        log.info("매수 취소: {}", deleteResponse.body().toString());
                    } else {
                        log.error("매수 취소 에러 :{}", deleteResponse.errorBody().byteString().toString());
                    }
                } catch (IOException e) {
                    log.error("매수 취소 에러: {}", e.getMessage());
                }
            }

            // 최종적으로 거래가 끝났거나 거래가 없다면 환전해서 밸런스를 맞춰주자.
            exchangeProfit();

            UpbitTransactionCached.TICKET = null;
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

    private Response<UpbitOrderResponse> bidingMarket(UpbitOrderbookItem askitem, BigDecimal amount, String pair) throws IOException {

        // 매수
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("bid")
                .price(amount.toPlainString())
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
                .volume(amount.divide(BigDecimal.valueOf(biditem.getAsk_price())).toPlainString())
                .ord_type("market")
                .build();

        return upbitApiRestClient.askOrder(request).execute();
    }

}
