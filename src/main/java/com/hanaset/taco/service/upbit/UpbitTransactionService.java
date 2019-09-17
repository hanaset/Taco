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
    private final Double profit = 0.35;
    private final int DELAY = 3000;

    public UpbitTransactionService(UpbitApiRestClient upbitApiRestClient) {
        this.upbitApiRestClient = upbitApiRestClient;
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
                            .bid_market(bidResponse.body().getMarket())
                            .ask_market("BTC-" + pair)
                            .bidOrderbookItem(krwItem)
                            .askOrderbookItem(btcItem)
                            .amount(BigDecimal.valueOf(amount))
                            .real_amount(BigDecimal.valueOf(base_amount))
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
                            .bid_market(bidResponse.body().getMarket())
                            .ask_market("KRW-" + pair)
                            .bidOrderbookItem(btcItem)
                            .askOrderbookItem(krwItem)
                            .amount(BigDecimal.valueOf(amount))
                            .real_amount(BigDecimal.valueOf(base_amount))
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

        UpbitTicket ticket = UpbitTransactionCached.TICKET;
        BigDecimal amount = ticket.getReal_amount();

        log.info("채결 내용: {}", upbitTrade);
        log.info("내 주문 내역: {}", ticket);

        if (!upbitTrade.getAsk_bid().equals("BID")
                || upbitTrade.getTrade_price().compareTo(BigDecimal.valueOf(ticket.getBidOrderbookItem().getBid_price())) != 0
                //|| upbitTrade.getTrade_volume().compareTo(ticket.getAmount()) != 0
                || amount.min(upbitTrade.getTrade_volume()).compareTo(ticket.getAmount()) <= 0
        ) { // 수량 금액 체크 추가

            try {
                log.info("매수 취소: {}", orderDeleting(ticket.getUuid()).body().toString());
            } catch (IOException e) {
                log.error("매수 취소 에러: {}", e.getMessage());
            }
            UpbitTransactionCached.TICKET = null;
            UpbitTransactionCached.LOCK = false;

            return;
        }

        ticket.setReal_amount(amount.min(upbitTrade.getTrade_volume()));

        if (upbitTrade.getTrade_volume().compareTo(ticket.getAmount()) != 0)
            return;

        try {

            Response<UpbitOrderResponse> askResponse = asking(ticket.getAskOrderbookItem(), ticket.getAmount(), ticket.getAsk_market());

            if (askResponse.isSuccessful()) {
                log.info("매도:{}", askResponse.body());

                UpbitOrderbookItem converItem = new UpbitOrderbookItem();
                converItem.setAsk_price(OrderbookCached.UPBIT_BTC.get("ask").doubleValue());
                converItem.setBid_price(OrderbookCached.UPBIT_BTC.get("bid").doubleValue());

                if (ticket.getBid_market().contains("KRW")) {

                    log.info("환전:{}", askingMarket(converItem, ticket.getAmount().multiply(BigDecimal.valueOf(ticket.getAskOrderbookItem().getAsk_price())), "KRW-BTC").body().toString());

                } else if (ticket.getBid_market().contains("BTC")) {

                    log.info("환전:{}", bidingMarket(converItem, ticket.getAmount().multiply(BigDecimal.valueOf(ticket.getBidOrderbookItem().getBid_price())), "KRW-BTC").body().toString());
                }

            } else {
                log.error("매도 에러: {}", askResponse.errorBody().byteString().toString());

                try {
                    log.info("매수 취소: {}", orderDeleting(ticket.getUuid()).body().toString());
                } catch (IOException e) {
                    log.error("매수 취소 에러: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage());
        } finally {
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
