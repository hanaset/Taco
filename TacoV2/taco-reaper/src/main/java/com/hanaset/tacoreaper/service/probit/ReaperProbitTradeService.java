package com.hanaset.tacoreaper.service.probit;

import com.hanaset.tacocommon.api.probit.ProbitApiRestClient;
import com.hanaset.tacocommon.api.probit.model.*;
import com.hanaset.tacocommon.api.upbit.model.UpbitTrade;
import com.hanaset.tacocommon.entity.reaper.ReaperAssetEntity;
import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacocommon.model.TacoErrorCode;
import com.hanaset.tacocommon.repository.reaper.ReaperAssetRepository;
import com.hanaset.tacocommon.utils.PairUtils;
import com.hanaset.tacoreaper.cached.ReaperTradeCached;
import com.hanaset.tacoreaper.model.ReaperPair;
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
    private ReaperAssetRepository reaperAssetRepository;

    public ReaperProbitTradeService(ProbitApiRestClient probitApiRestClient,
                                    ReaperAssetRepository reaperAssetRepository) {
        this.probitApiRestClient = probitApiRestClient;
        this.reaperAssetRepository = reaperAssetRepository;
    }

    @Async
    @Synchronized
    public void updateUpbitData(UpbitTrade trade) {

        String pair = PairUtils.getPair(trade.getCode());

        ReaperPair askProbitPair = ReaperTradeCached.ASK_PAIR_VALUE.get(pair);
        ReaperPair bidProbitPair = ReaperTradeCached.BID_PAIR_VALUE.get(pair);

        if(askProbitPair != null && bidProbitPair != null) {

            if (askProbitPair.getPrice().compareTo(bidProbitPair.getPrice()) == 0)
                if (trade.getAskBid().equals("BID")) {
                    trade.setTradePrice(trade.getTradePrice().add(ReaperTradeCached.TRADE_CONDITION.getUnit()));
                } else {
                    trade.setTradePrice(trade.getChangePrice().subtract(ReaperTradeCached.TRADE_CONDITION.getUnit()));
                }
        }

        if (trade.getAskBid().equals("BID")) {
            ReaperTradeCached.ASK_PAIR_VALUE.put(PairUtils.getPair(trade.getCode()), ReaperPair.builder()
                    .side("sell")
                    .price(trade.getTradePrice())
                    .build());
        } else {
            ReaperTradeCached.BID_PAIR_VALUE.put(PairUtils.getPair(trade.getCode()), ReaperPair.builder()
                    .side("buy")
                    .price(trade.getTradePrice())
                    .build());
        }

        //log.info("ASK : {} / BID : {}", ReaperTradeCached.ASK_PAIR_VALUE, ReaperTradeCached.BID_PAIR_VALUE);
    }

    public void tradeFlashingProbit(String pair) {

        List<ProbitBalance> balances = probitApiRestClient.getBalance();
        Map<String, BigDecimal> balanceMap = balances.stream().filter(balance -> balance.getCurrencyId().equals(pair) || balance.getCurrencyId().equals("KRW"))
                .collect(Collectors.toMap(ProbitBalance::getCurrencyId, probitBalance -> BigDecimal.valueOf(Double.parseDouble(probitBalance.getAvailable()))));

        ReaperPair askProbitPair = ReaperTradeCached.ASK_PAIR_VALUE.get(pair);
        ReaperPair bidProbitPair = ReaperTradeCached.BID_PAIR_VALUE.get(pair);

        if(askProbitPair == null || bidProbitPair == null) {
            return;
        }

        ReaperTradeCondition condition = ReaperTradeCached.TRADE_CONDITION;
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
            Thread.sleep(1000 * condition.getInterval());
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        ProbitOrderCancelRequest probitOrderCancelRequest = ProbitOrderCancelRequest.builder()
                .marketId(response.getMarketId())
                .orderId(response.getId())
                .build();

        probitApiRestClient.cancelOrder(probitOrderCancelRequest);
    }

    public void tradeProbit(String pair) {

        List<ProbitBalance> balances = probitApiRestClient.getBalance();
        Map<String, BigDecimal> balanceMap = balances.stream().filter(balance -> balance.getCurrencyId().equals(pair) || balance.getCurrencyId().equals("KRW"))
                .collect(Collectors.toMap(ProbitBalance::getCurrencyId, probitBalance -> BigDecimal.valueOf(Double.parseDouble(probitBalance.getAvailable()))));

        ReaperPair askProbitPair = ReaperTradeCached.ASK_PAIR_VALUE.get(pair);
        ReaperPair bidProbitPair = ReaperTradeCached.BID_PAIR_VALUE.get(pair);

        if(askProbitPair == null || bidProbitPair == null) {
            return;
        }

        ReaperTradeCondition condition = ReaperTradeCached.TRADE_CONDITION;

        //System.out.println(ReaperTradeCached.PROBIT_RESPONSE);

        if(!ReaperTradeCached.PROBIT_RESPONSE.containsKey(pair)) { // 주문 내역이 없을 경우 주문
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

            System.out.println(request);
            ReaperTradeCached.PROBIT_RESPONSE.put(pair,probitApiRestClient.order(request));

        }else { // 주문내역이 있을 경우

            ProbitOrderResponse response = ReaperTradeCached.PROBIT_RESPONSE.get(pair);

            ProbitOrderInfoResponse orderInfoResponse = probitApiRestClient.getOrder(response.getMarketId(), response.getId());

            if(orderInfoResponse.getFilledQuantity().equals("0")) { // 주문 체결이 되지 않았으나 호가창이 변할 경우

                BigDecimal limitPrice = BigDecimal.valueOf(Double.parseDouble(response.getLimitPrice()));

                if ((response.getSide().equals("sell") && limitPrice.compareTo(askProbitPair.getPrice()) != 0)  ||
                        (response.getSide().equals("buy") && limitPrice.compareTo(bidProbitPair.getPrice()) != 0)) {
                    ProbitOrderCancelRequest probitOrderCancelRequest = ProbitOrderCancelRequest.builder()
                            .marketId(ReaperTradeCached.PROBIT_RESPONSE.get(pair).getMarketId())
                            .orderId(ReaperTradeCached.PROBIT_RESPONSE.get(pair).getId())
                            .build();

                    ReaperTradeCached.PROBIT_RESPONSE.remove(pair);
                    probitApiRestClient.cancelOrder(probitOrderCancelRequest);
                }
            } else { // 주문이 채결 되었을 경우
                    // 미체결을 없애기 위해 취소 한번
                ProbitOrderCancelRequest probitOrderCancelRequest = ProbitOrderCancelRequest.builder()
                        .marketId(ReaperTradeCached.PROBIT_RESPONSE.get(pair).getMarketId())
                        .orderId(ReaperTradeCached.PROBIT_RESPONSE.get(pair).getId())
                        .build();

                ReaperTradeCached.PROBIT_RESPONSE.remove(pair);
                probitApiRestClient.cancelOrder(probitOrderCancelRequest);
            }
        }

    }

    public void init(String pair) {
        initProbitCondition(pair);
    }

    private void initProbitCondition(String pair) {

        ReaperAssetEntity entity = reaperAssetRepository.findByAsset(pair)
                .orElseThrow(()-> new TacoResponseException(TacoErrorCode.ASSET_ERROR, "DB에 해당 ASSET이 존재하지 않습니다."));

        ReaperTradeCached.TRADE_CONDITION = ReaperTradeCondition.builder()
                .pair(entity.getAsset())
                .interval(entity.getInterval())
                .orderMaxVolume(entity.getOrderMaxVolume())
                .orderMinVolume(entity.getOrderMinVolume())
                .baseAsset(entity.getBaseAsset())
                .precision(entity.getPrecision())
                .unit(entity.getUnit())
                .tradingVolume(entity.getTradingVolume())
                .build();
    }

    private ProbitOrderRequest createOrderASK(String pair) {

        ReaperPair bidProbitPair = ReaperTradeCached.BID_PAIR_VALUE.get(pair);

        ReaperTradeCondition condition = ReaperTradeCached.TRADE_CONDITION;

        return ProbitOrderRequest.builder()
                .makretId(pair + "-KRW") // PROBIT의 마켓 ID는 XRP-KRW 형식이다.
                .side(bidProbitPair.getSide())
                .type("limit")
                .timeInForce("gtc")
                .limitPrice(bidProbitPair.getPrice().toPlainString())
                .quantity(condition.getTradingVolume().divide(bidProbitPair.getPrice(), condition.getPrecision(), RoundingMode.DOWN).toPlainString())
                .build();
    }

    private ProbitOrderRequest createOrderBID(String pair, Map<String, BigDecimal> balanceMap) {

        ReaperPair askProbitPair = ReaperTradeCached.ASK_PAIR_VALUE.get(pair);

        return ProbitOrderRequest.builder()
                .makretId(pair + "-KRW") // PROBIT의 마켓 ID는 XRP-KRW 형식이다.
                .side(askProbitPair.getSide())
                .type("limit")
                .timeInForce("gtc")
                .limitPrice(askProbitPair.getPrice().toPlainString())
                .quantity(balanceMap.get(pair).toPlainString())
                .build();

    }

}
