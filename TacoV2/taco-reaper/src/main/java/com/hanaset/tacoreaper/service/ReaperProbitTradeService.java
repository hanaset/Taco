package com.hanaset.tacoreaper.service;

import com.hanaset.tacocommon.api.probit.ProbitApiRestClient;
import com.hanaset.tacocommon.api.probit.model.ProbitBalance;
import com.hanaset.tacocommon.api.probit.model.ProbitOrderCancelRequest;
import com.hanaset.tacocommon.api.probit.model.ProbitOrderRequest;
import com.hanaset.tacocommon.api.probit.model.ProbitOrderResponse;
import com.hanaset.tacocommon.api.upbit.model.UpbitTrade;
import com.hanaset.tacocommon.utils.PairUtils;
import com.hanaset.tacoreaper.cached.ReaperProbitCached;
import com.hanaset.tacoreaper.model.ReaperProbitPair;
import com.hanaset.tacoreaper.model.ReaperTradeCondition;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("Duplicates")
public class ReaperProbitTradeService {

    private ProbitApiRestClient probitApiRestClient;

    public ReaperProbitTradeService(ProbitApiRestClient probitApiRestClient) {
        this.probitApiRestClient = probitApiRestClient;
    }

    @Async
    @Synchronized
    public void updateUpbitData(UpbitTrade trade) {

        if (trade.getAsk_bid().equals("BID")) {
            ReaperProbitCached.ASK_PAIR_VALUE.put(PairUtils.getPair(trade.getCode()), ReaperProbitPair.builder()
                    .side("sell")
                    .price(trade.getTrade_price())
                    .build());
        } else {
            ReaperProbitCached.BID_PAIR_VALUE.put(PairUtils.getPair(trade.getCode()), ReaperProbitPair.builder()
                    .side("buy")
                    .price(trade.getTrade_price())
                    .build());
        }

        //log.info("ASK : {} / BID : {}", ReaperProbitCached.ASK_PAIR_VALUE, ReaperProbitCached.BID_PAIR_VALUE);
    }

    public void tradeProbit(String pair) {
        List<ProbitBalance> balances = probitApiRestClient.getBalance();
        Map<String, BigDecimal> balanceMap = balances.stream().filter(balance -> balance.getCurrencyId().equals(pair) || balance.getCurrencyId().equals("KRW"))
                .collect(Collectors.toMap(ProbitBalance::getCurrencyId, probitBalance -> BigDecimal.valueOf(Double.parseDouble(probitBalance.getAvailable()))));

        ReaperProbitPair askProbitPair = ReaperProbitCached.ASK_PAIR_VALUE.get(pair);
        ReaperProbitPair bidProbitPair = ReaperProbitCached.BID_PAIR_VALUE.get(pair);

        if(askProbitPair == null || bidProbitPair == null) {
            return;
        }

        ReaperTradeCondition condition = ReaperProbitCached.TRADE_CONDITION;
        ProbitOrderRequest request;

        if (!balanceMap.containsKey(pair)) { // 해당 페어에 대해 보유하고 있지 않을 경우

            request = createOrderASK(pair);

        } else { // 해당 페어를 보유하지 못하고 있을 경우

            if (balanceMap.get(pair).multiply(bidProbitPair.getPrice()).compareTo(condition.getOrderMinVolume()) <= 0) { // 최소 금액보다 Pair 보유량이 적을 경우

                request = createOrderASK(pair);

            } else {

                request = createOrderBID(pair, balanceMap);

            }
        }

        ProbitOrderResponse response = probitApiRestClient.order(request);

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        ProbitOrderCancelRequest probitOrderCancelRequest = ProbitOrderCancelRequest.builder()
                .marketId(response.getMarketId())
                .orderId(response.getId())
                .build();

        probitApiRestClient.cancelOrder(probitOrderCancelRequest);
    }

    public void init() {
        initProbitCondition();
    }

    private void initProbitCondition() {

        ReaperProbitCached.TRADE_CONDITION = ReaperTradeCondition.builder()
                .pair("XRP")
                .interval(1)
                .orderMaxVolume(BigDecimal.valueOf(5000))
                .orderMinVolume(BigDecimal.valueOf(500))
                .baseAsset("KRW")
                .precision(2)
                .unit(BigDecimal.valueOf(1))
                .tradingVolume(BigDecimal.valueOf(5000))
                .build();
    }

    private ProbitOrderRequest createOrderASK(String pair) {

        ReaperProbitPair askProbitPair = ReaperProbitCached.ASK_PAIR_VALUE.get(pair);
        ReaperProbitPair bidProbitPair = ReaperProbitCached.BID_PAIR_VALUE.get(pair);

        ReaperTradeCondition condition = ReaperProbitCached.TRADE_CONDITION;

        return ProbitOrderRequest.builder()
                .makretId(pair + "-KRW") // PROBIT의 마켓 ID는 XRP-KRW 형식이다.
                .side(bidProbitPair.getSide())
                .type("limit")
                .timeInForce("gtc")
                .limitPrice(bidProbitPair.getPrice().compareTo(askProbitPair.getPrice()) == 0 ? bidProbitPair.getPrice().subtract(condition.getUnit()).toPlainString() : bidProbitPair.getPrice().toPlainString()) // 매수 매도 호가가 같을 경우 -1
                .quantity(condition.getTradingVolume().divide(bidProbitPair.getPrice(), condition.getPrecision(), RoundingMode.DOWN).toPlainString())
                .build();
    }

    private ProbitOrderRequest createOrderBID(String pair, Map<String, BigDecimal> balanceMap) {

        ReaperProbitPair askProbitPair = ReaperProbitCached.ASK_PAIR_VALUE.get(pair);
        ReaperProbitPair bidProbitPair = ReaperProbitCached.BID_PAIR_VALUE.get(pair);

        ReaperTradeCondition condition = ReaperProbitCached.TRADE_CONDITION;

        return ProbitOrderRequest.builder()
                .makretId(pair + "-KRW") // PROBIT의 마켓 ID는 XRP-KRW 형식이다.
                .side(askProbitPair.getSide())
                .type("limit")
                .timeInForce("gtc")
                .limitPrice(askProbitPair.getPrice().compareTo(bidProbitPair.getPrice()) == 0 ? askProbitPair.getPrice().add(condition.getUnit()).toPlainString() : askProbitPair.getPrice().toPlainString()) // 매수 매도 호가가 같은 경우 +1
                .quantity(balanceMap.get(pair).toPlainString())
                .build();

    }

}
