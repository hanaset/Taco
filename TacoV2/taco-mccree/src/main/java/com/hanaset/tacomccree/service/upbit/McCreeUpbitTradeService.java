package com.hanaset.tacomccree.service.upbit;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.api.TacoResponse;
import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.api.upbit.model.UpbitAccount;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderRequest;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderResponse;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.tacocommon.cache.OrderbookCached;
import com.hanaset.tacocommon.entity.mccree.McCreeTransactionEntity;
import com.hanaset.tacocommon.model.TacoErrorCode;
import com.hanaset.tacocommon.repository.mccree.McCreeTransactionRepository;
import com.hanaset.tacomccree.config.PairConfig;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("Duplicates")
public class McCreeUpbitTradeService {

    private final UpbitApiRestClient upbitApiRestClient;
    private final McCreeTransactionRepository mcCreeTransactionRepository;

    public McCreeUpbitTradeService(UpbitApiRestClient upbitApiRestClient,
                                   McCreeTransactionRepository mcCreeTransactionRepository) {
        this.upbitApiRestClient = upbitApiRestClient;
        this.mcCreeTransactionRepository = mcCreeTransactionRepository;
    }

    public void init(PairConfig pairConfig) {

        List<UpbitOrderResponse> orderList = Optional.ofNullable(getOrders(pairConfig.getMarket())).orElse(Lists.newArrayList());

        log.info("[주문 내역 초기화]");
        orderList.stream().forEach(upbitOrderResponse -> {
            System.out.println(upbitOrderResponse);
            orderDeleting(upbitOrderResponse.getUuid());
        });

    }

    @Synchronized
    public void trade(PairConfig pairConfig) {

        UpbitOrderbookItem item = OrderbookCached.UPBIT.get(pairConfig.getMarket());
        if (item == null) {
            log.error("OrderBook Null Error");
            return;
        }

        List<UpbitOrderResponse> orderList = Optional.ofNullable(getOrders(pairConfig.getMarket())).orElse(Lists.newArrayList());

        if (OrderbookCached.UPBIT_CHANGE.get(pairConfig.getMarket())) {
            log.info("[{} [전체 주문 내역 삭제]", pairConfig.getMarket());
            orderList.stream().forEach(upbitOrderResponse -> {
                System.out.println(upbitOrderResponse);
                orderDeleting(upbitOrderResponse.getUuid());
            });
            OrderbookCached.UPBIT_CHANGE.put(pairConfig.getMarket(), false);
            return;
        }

        if (BigDecimal.valueOf(item.getBid_price() * item.getBid_size()).compareTo(pairConfig.getLimitPrice()) <= 0) {
            log.info("[{} 매수 주문 내역 삭제]", pairConfig.getMarket());
            List<UpbitOrderResponse> deleteList = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("bid"))
                    .collect(Collectors.toList());
            deleteList.stream().forEach(upbitOrderResponse -> {
                System.out.println(upbitOrderResponse);
                orderDeleting(upbitOrderResponse.getUuid());
            });
        }

        if (BigDecimal.valueOf(item.getAsk_price() * item.getAsk_size()).compareTo(pairConfig.getLimitPrice()) <= 0) {
            log.info("[{} 매도 주문 내역 삭제]", pairConfig.getMarket());
            List<UpbitOrderResponse> deleteList = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("ask"))
                    .collect(Collectors.toList());
            deleteList.stream().forEach(upbitOrderResponse -> {
                System.out.println(upbitOrderResponse);
                orderDeleting(upbitOrderResponse.getUuid());
            });
        }

        List<UpbitAccount> upbitAccounts = getAccounts();
        Map<String, UpbitAccount> accountMap = upbitAccounts.stream().collect(Collectors.toMap(UpbitAccount::getCurrency, upbitAccount -> upbitAccount));
        UpbitAccount account = Optional.ofNullable(accountMap.get(pairConfig.getAsset()))
                .orElse(
                        UpbitAccount.builder().
                                currency(pairConfig.getAsset())
                                .balance(BigDecimal.ZERO)
                                .build()
                ); // 보유량이 없을 경우 처리

        BigDecimal askingVolume = BigDecimal.valueOf(
                orderList.stream()
                        .filter(upbitOrderResponse -> upbitOrderResponse.getRemaining_volume() != 0 && upbitOrderResponse.getSide().equals("ask"))
                        .map(UpbitOrderResponse::getRemaining_volume).reduce(Double::sum)
                        .orElse(Double.valueOf(0)));

        BigDecimal biddingVolume = BigDecimal.valueOf(
                orderList.stream()
                        .filter(upbitOrderResponse -> upbitOrderResponse.getRemaining_volume() != 0 && upbitOrderResponse.getSide().equals("bid"))
                        .map(UpbitOrderResponse::getRemaining_volume).reduce(Double::sum)
                        .orElse(Double.valueOf(0)));

        if (askingVolume.compareTo(BigDecimal.ZERO) == 0 && biddingVolume.compareTo(BigDecimal.ZERO) == 0) { // 처음 거래일 경우

            // 초기 물량에 댜한 설명 수정 필요
            BigDecimal askVolume = getInitAskVolume(pairConfig, account);
            BigDecimal bidVolume = getInitBidVolume(pairConfig, account);

            if (askVolume.compareTo(BigDecimal.ZERO) > 0)
                asking(item, getInitAskVolume(pairConfig, account), pairConfig);

            if (bidVolume.compareTo(BigDecimal.ZERO) > 0)
                bidding(item, getInitBidVolume(pairConfig, account), pairConfig);

        } else {

            BigDecimal askVolume = getAskVolume(askingVolume, biddingVolume, pairConfig);
            BigDecimal bidVolume = getBidVolume(askingVolume, biddingVolume, pairConfig);

            if (askVolume.compareTo(BigDecimal.ZERO) > 0)
                asking(item, askVolume, pairConfig);

            if (bidVolume.compareTo(BigDecimal.ZERO) > 0)
                bidding(item, bidVolume, pairConfig);
        }

    }

    @Async
    public void webSocketTrade(PairConfig pairConfig, Boolean change) {

        if (OrderbookCached.UPBIT_LOCKS.get(pairConfig.getMarket())) {
            log.info("{} 현재 LOCK 상태입니다.", pairConfig.getMarket());
            return;
        }
        OrderbookCached.UPBIT_LOCKS.put(pairConfig.getMarket(), true);

        UpbitOrderbookItem item = OrderbookCached.UPBIT.get(pairConfig.getMarket());
        if (item == null) {
            log.error("OrderBook Null Error");
            OrderbookCached.UPBIT_LOCKS.put(pairConfig.getMarket(), false);
            return;
        }

        List<UpbitOrderResponse> orderList = Optional.ofNullable(getOrders(pairConfig.getMarket())).orElse(Lists.newArrayList());

        if (change) {
            log.info("[{} [전체 주문 내역 삭제]", pairConfig.getMarket());
            orderList.stream().forEach(upbitOrderResponse -> {
                System.out.println(upbitOrderResponse);
                orderDeleting(upbitOrderResponse.getUuid());
            });
            OrderbookCached.UPBIT_CHANGE.put(pairConfig.getMarket(), false);
            OrderbookCached.UPBIT_LOCKS.put(pairConfig.getMarket(), false);
            return;
        }

        if (BigDecimal.valueOf(item.getAsk_price() * item.getAsk_size()).compareTo(pairConfig.getLimitPrice()) <= 0 && orderList.size() != 0) {
            List<UpbitOrderResponse> deleteList = orderList.stream()
                    .filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("ask") && upbitOrderResponse.getPrice().equals(item.getAsk_price()))
                    .collect(Collectors.toList());

            if (deleteList.size() != 0) {
                log.info("[{} 매도 주문 내역 삭제 => {}, {}]", pairConfig.getMarket(), item.getAsk_size(), BigDecimal.valueOf(item.getAsk_price() * item.getAsk_size()).toPlainString());
                deleteList = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("ask")).collect(Collectors.toList());
                deleteList.forEach(upbitOrderResponse -> {
                    System.out.println(upbitOrderResponse);
                    orderDeleting(upbitOrderResponse.getUuid());
                });
            }

        } else if (BigDecimal.valueOf(item.getAsk_price() * item.getAsk_size()).compareTo(pairConfig.getLimitPrice()) > 0 && orderList.size() != 0) {
            List<UpbitOrderResponse> deleteList = orderList.stream()
                    .filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("ask") && !upbitOrderResponse.getPrice().equals(item.getAsk_price())).collect(Collectors.toList());

            if (deleteList.size() != 0) {
                log.info("[{} 매도 주문 내역 삭제 => {}, {}]", pairConfig.getMarket(), item.getAsk_size(), BigDecimal.valueOf(item.getAsk_price() * item.getAsk_size()).toPlainString());
                deleteList = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("ask")).collect(Collectors.toList());
                deleteList.forEach(upbitOrderResponse -> {
                    System.out.println(upbitOrderResponse);
                    orderDeleting(upbitOrderResponse.getUuid());
                });
            }
        }

        if (BigDecimal.valueOf(item.getBid_price() * item.getBid_size()).compareTo(pairConfig.getLimitPrice()) <= 0 && orderList.size() != 0) {
            List<UpbitOrderResponse> deleteList = orderList.stream()
                    .filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("bid") && upbitOrderResponse.getPrice().equals(item.getBid_price()))
                    .collect(Collectors.toList());

            if (deleteList.size() != 0) {
                log.info("[{} 매수 주문 내역 삭제 => {}, {}]", pairConfig.getMarket(), item.getBid_size(), BigDecimal.valueOf(item.getBid_price() * item.getBid_size()).toPlainString());
                deleteList = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("bid")).collect(Collectors.toList());
                deleteList.forEach(upbitOrderResponse -> {
                    System.out.println(upbitOrderResponse);
                    orderDeleting(upbitOrderResponse.getUuid());
                });
            }

        } else if(BigDecimal.valueOf(item.getBid_price() * item.getBid_size()).compareTo(pairConfig.getLimitPrice()) > 0 && orderList.size() != 0) {
            List<UpbitOrderResponse> deleteList = orderList.stream()
                    .filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("bid") && !upbitOrderResponse.getPrice().equals(item.getBid_price())).collect(Collectors.toList());

            if (deleteList.size() != 0) {
                log.info("[{} 매수 주문 내역 삭제 => {}, {}]", pairConfig.getMarket(), item.getBid_size(), BigDecimal.valueOf(item.getBid_price() * item.getBid_size()).toPlainString());
                deleteList = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("bid")).collect(Collectors.toList());
                deleteList.forEach(upbitOrderResponse -> {
                    System.out.println(upbitOrderResponse);
                    orderDeleting(upbitOrderResponse.getUuid());
                });
            }
        }


        Double askingAmount = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("ask")).map(UpbitOrderResponse::getRemaining_volume).reduce(Double::sum).orElse(Double.valueOf(0));
        Double biddingAmount = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getSide().equals("bid")).map(UpbitOrderResponse::getRemaining_volume).reduce(Double::sum).orElse(Double.valueOf(0));

        if (askingAmount == 0 && biddingAmount == 0) {

            List<UpbitAccount> upbitAccounts = getAccounts();
            Map<String, UpbitAccount> accountMap = upbitAccounts.stream().collect(Collectors.toMap(UpbitAccount::getCurrency, upbitAccount -> upbitAccount));
            UpbitAccount account = Optional.ofNullable(accountMap.get(pairConfig.getAsset()))
                    .orElse(
                            UpbitAccount.builder().
                                    currency(pairConfig.getAsset())
                                    .balance(BigDecimal.ZERO)
                                    .build()
                    ); // 보유량이 없을 경우 처리

            asking(item, getInitAskVolume(pairConfig, account), pairConfig);
            bidding(item, getInitBidVolume(pairConfig, account), pairConfig);

        } else if (pairConfig.getVolume().multiply(BigDecimal.valueOf(2)).compareTo(BigDecimal.valueOf(askingAmount + biddingAmount)) != 0) {

            if (askingAmount.compareTo(pairConfig.getVolume().doubleValue()) != 0 && askingAmount > 0) {
                asking(item, pairConfig.getVolume().subtract(BigDecimal.valueOf(askingAmount)), pairConfig);
                bidding(item, pairConfig.getVolume().subtract(BigDecimal.valueOf(askingAmount)), pairConfig);

                // 거래 내역 기록
            }

            if (biddingAmount.compareTo(pairConfig.getVolume().doubleValue()) != 0 && biddingAmount > 0) {
                asking(item, pairConfig.getVolume().subtract(BigDecimal.valueOf(biddingAmount)), pairConfig);
                bidding(item, pairConfig.getVolume().subtract(BigDecimal.valueOf(biddingAmount)), pairConfig);

                // 거래 내역 기록
            }

            List<McCreeTransactionEntity> mcCreeTransactionEntities = orderList.stream().filter(upbitOrderResponse -> upbitOrderResponse.getExecuted_volume() != 0).map(upbitOrderResponse ->
                    McCreeTransactionEntity.builder()
                        .uuid(upbitOrderResponse.getUuid())
                        .sid(upbitOrderResponse.getSide())
                        .ordType(upbitOrderResponse.getOrd_type())
                        .price(BigDecimal.valueOf(upbitOrderResponse.getPrice()))
                        .avgPrice(BigDecimal.valueOf(upbitOrderResponse.getAvg_price()))
                        .sid(upbitOrderResponse.getState())
                        .market(upbitOrderResponse.getMarket())
                        .createAt(upbitOrderResponse.getCreated_at())
                        .volume(BigDecimal.valueOf(upbitOrderResponse.getVolume()))
                        .remainingVolume(BigDecimal.valueOf(upbitOrderResponse.getRemaining_volume()))
                        .reservedFee(BigDecimal.valueOf(upbitOrderResponse.getReserved_fee()))
                        .paidFee(BigDecimal.valueOf(upbitOrderResponse.getPaid_fee()))
                        .locked(BigDecimal.valueOf(upbitOrderResponse.getLocked()))
                        .executedVolume(BigDecimal.valueOf(upbitOrderResponse.getExecuted_volume()))
                        .build()

            ).collect(Collectors.toList());

            mcCreeTransactionRepository.saveAll(mcCreeTransactionEntities);
        }

        OrderbookCached.UPBIT_LOCKS.put(pairConfig.getMarket(), false);
    }

    private BigDecimal getInitBidVolume(PairConfig pairConfig, UpbitAccount account) {

        BigDecimal gap = pairConfig.getVolume().subtract(account.getBalance());
        return gap.compareTo(BigDecimal.ZERO) > 0 ? pairConfig.getVolume().add(gap) : pairConfig.getVolume();
    }

    private BigDecimal getInitAskVolume(PairConfig pairConfig, UpbitAccount account) {

        return account.getBalance().compareTo(pairConfig.getVolume()) < 0 ? account.getBalance() : pairConfig.getVolume();
    }

    private BigDecimal getBidVolume(BigDecimal askingVolume, BigDecimal biddingVolume, PairConfig pairConfig) {

        BigDecimal bidVolume = pairConfig.getVolume().subtract(askingVolume).add(pairConfig.getVolume()).subtract(biddingVolume);
        // 기본 수량 + 판매된 수량 - 현재 사고 있는 수량 = 사야하는 수량

        return bidVolume;

    }

    private BigDecimal getAskVolume(BigDecimal askingVolume, BigDecimal biddingVolume, PairConfig pairConfig) {

        BigDecimal askVolume = pairConfig.getVolume().subtract(biddingVolume).add(pairConfig.getVolume()).subtract(askingVolume);
        // 기본 수량 + 구매수량 - 현재 팔고 있는 수량 = 팔아야하는 수량

        return askVolume;

    }


    private List<UpbitOrderResponse> getOrders(String market) {

        Response<List<UpbitOrderResponse>> response = null;

        try {
            response = upbitApiRestClient.getOrders(market).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "getOrder Error");
        } catch (IOException e) {
            log.error("get Order IOException : {}", e.getMessage());
        }

        return response.body();
    }


    @Async
    public UpbitOrderResponse bidding(UpbitOrderbookItem askitem, BigDecimal amount, PairConfig pairConfig) {

        BigDecimal price = BigDecimal.valueOf(askitem.getBid_price()).add(pairConfig.getAskPrice());
        if (BigDecimal.valueOf(askitem.getBid_price() * askitem.getBid_size()).compareTo(pairConfig.getLimitPrice()) <= 0)
            price = price.subtract(pairConfig.getUnit());

        // 매수
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pairConfig.getMarket())
                .side("bid")
                .price(price.toPlainString())
                .volume(amount.toPlainString())
                .ord_type("limit")
                .build();

        Response<UpbitOrderResponse> response = null;

        try {
            response = upbitApiRestClient.createOrder(request).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Bid Error");
            log.info("[매수 요청] {} : {}, {}", pairConfig.getMarket(), price.toPlainString(), amount.toPlainString());
        } catch (IOException e) {
            log.error("bidding IOException : {}", e.getMessage());
        }

        return response.body();

    }

    @Async
    public UpbitOrderResponse asking(UpbitOrderbookItem biditem, BigDecimal amount, PairConfig pairConfig) {

        BigDecimal price = BigDecimal.valueOf(biditem.getAsk_price()).add(pairConfig.getAskPrice());
        if (BigDecimal.valueOf(biditem.getAsk_price() * biditem.getAsk_size()).compareTo(pairConfig.getLimitPrice()) <= 0)
            price = price.add(pairConfig.getUnit());

        // 매도
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pairConfig.getMarket())
                .side("ask")
                .price(price.toPlainString())
                .volume(amount.toPlainString())
                .ord_type("limit")
                .build();

        Response<UpbitOrderResponse> response = null;

        try {
            response = upbitApiRestClient.createOrder(request).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Ask Error");
            log.info("[매도 요청] {} : {}, {}", pairConfig.getMarket(), price.toPlainString(), amount.toPlainString());
        } catch (IOException e) {
            log.error("asking IOException : {}", e.getMessage());
        }

        return response.body();
    }

    public UpbitOrderResponse orderDeleting(String uuid) {

        Response<UpbitOrderResponse> response = null;

        try {
            response = upbitApiRestClient.deleteOrder(uuid).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Delete Error");
        } catch (IOException e) {
            log.error("Delete IOException : {}", e.getMessage());
        }

        return response.body();
    }

    public List<UpbitAccount> getAccounts() {

        Response<List<UpbitAccount>> response = null;

        try {
            response = upbitApiRestClient.getAccount().execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Get Account Error");
        } catch (IOException e) {
            log.error("Get Account IOException : {}", e.getMessage());
        }

        return response.body();
    }
}
