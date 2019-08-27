package com.hanaset.taco.api.binance.impl;


import com.hanaset.taco.api.binance.BinanceApiRestClient;
import com.hanaset.taco.api.binance.constant.BinanceApiConstants;
import com.hanaset.taco.api.binance.domain.account.*;
import com.hanaset.taco.api.binance.domain.account.request.AllOrdersRequest;
import com.hanaset.taco.api.binance.domain.account.request.CancelOrderRequest;
import com.hanaset.taco.api.binance.domain.account.request.OrderRequest;
import com.hanaset.taco.api.binance.domain.account.request.OrderStatusRequest;
import com.hanaset.taco.api.binance.domain.event.ListenKey;
import com.hanaset.taco.api.binance.domain.market.*;
import io.reactivex.Single;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

import static com.hanaset.taco.api.binance.impl.BinanceApiServiceGenerator.executeSync;


/**
 * Implementation of Binance's REST API using Retrofit with synchronous/blocking method calls.
 */
public class BinanceApiRestClientImpl implements BinanceApiRestClient {

    private final BinanceApiService binanceApiService;

    private final BinanceApiServiceGenerator binanceApiServiceGenerator;

    public BinanceApiRestClientImpl(String apiKey, String secret) {
        binanceApiServiceGenerator = new BinanceApiServiceGenerator();
        binanceApiService = binanceApiServiceGenerator.createService(BinanceApiService.class, apiKey, secret);
    }

    // General endpoints

    @Override
    public void ping() {
        executeSync(binanceApiService.ping());
    }

    @Override
    public Long getServerTime() {
        return executeSync(binanceApiService.getServerTime()).getServerTime();
    }

    // Market Data endpoints

    @Override
    public BinanceOrderBook getOrderBook(String symbol, Integer limit) {
        return executeSync(binanceApiService.getOrderBook(symbol, limit));
    }

    @Override
    public Single<BinanceOrderBook> getOrderBookRx(String symbol, Integer limit) {
        return binanceApiService.getOrderBookRx(symbol, limit);
    }

    @Override
    public List<AggTrade> getAggTrades(String symbol, String fromId, Integer limit, Long startTime, Long endTime) {
        return executeSync(binanceApiService.getAggTrades(symbol, fromId, limit, startTime, endTime));
    }

    @Override
    public List<AggTrade> getAggTrades(String symbol) {
        return getAggTrades(symbol, null, null, null, null);
    }

    @Override
    public List<Object[]> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        return executeSync(binanceApiService.getCandlestickBars(symbol, interval.getIntervalId(), limit, startTime, endTime));
    }

    @Override
    public Single<List<Object[]>> getCandlestickBarsRx(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        return binanceApiService.getCandlestickBarsRx(symbol, interval.getIntervalId(), limit, startTime, endTime);
    }

    @Override
    public List<Object[]> getCandlestickBars(String symbol, CandlestickInterval interval) {
        return getCandlestickBars(symbol, interval, null, null, null);
    }

    @Override
    public TickerStatistics get24HrPriceStatistics(String symbol) {
        return executeSync(binanceApiService.get24HrPriceStatistics(symbol));
    }

    @Override
    public List<TickerPrice> getAllPrices() {
        return executeSync(binanceApiService.getLatestPrices());
    }

    @Override
    public Single<List<TickerPrice>> getAllPricesRx() {
        return binanceApiService.getLatestPricesRx();
    }

    @Override
    public Single<TickerPrice> getPrice(String symbol) {
        return binanceApiService.getPrice(symbol);
    }

    @Override
    public List<BookTicker> getBookTickers() {
        return executeSync(binanceApiService.getBookTickers());
    }

    @Override
    public NewOrderResponse newOrder(NewOrder order) {
        return executeSync(binanceApiService.newOrder(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getStopPrice(), order.getIcebergQty(),
                order.getRecvWindow(), order.getTimestamp()));
    }

    @Override
    public Single<NewOrderResponse> newOrderRx(NewOrder order) {
        return binanceApiService.newOrderRx(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getStopPrice(), order.getIcebergQty(),
                order.getRecvWindow(), order.getTimestamp());
    }

    @Override
    public void newOrderTest(NewOrder order) {
        executeSync(binanceApiService.newOrderTest(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getStopPrice(), order.getIcebergQty(),
                order.getRecvWindow(), order.getTimestamp()));
    }

    // Account endpoints

    @Override
    public Order getOrderStatus(OrderStatusRequest orderStatusRequest) {
        return executeSync(binanceApiService.getOrderStatus(orderStatusRequest.getSymbol(),
                orderStatusRequest.getOrderId(), orderStatusRequest.getOrigClientOrderId(),
                orderStatusRequest.getRecvWindow(), orderStatusRequest.getTimestamp()));
    }

    @Override
    public Single<Order> getOrderStatusRx(OrderStatusRequest orderStatusRequest) {
        return binanceApiService.getOrderStatusRx(orderStatusRequest.getSymbol(),
                orderStatusRequest.getOrderId(), orderStatusRequest.getOrigClientOrderId(),
                orderStatusRequest.getRecvWindow(), orderStatusRequest.getTimestamp());
    }

    @Override
    public void cancelOrder(CancelOrderRequest cancelOrderRequest) {
        executeSync(binanceApiService.cancelOrder(cancelOrderRequest.getSymbol(),
                cancelOrderRequest.getOrderId(), cancelOrderRequest.getOrigClientOrderId(), cancelOrderRequest.getNewClientOrderId(),
                cancelOrderRequest.getRecvWindow(), cancelOrderRequest.getTimestamp()));
    }

    @Override
    public Single<Map> cancelOrderRx(CancelOrderRequest cancelOrderRequest) {
        return binanceApiService.cancelOrderRx(cancelOrderRequest.getSymbol(),
                cancelOrderRequest.getOrderId(), cancelOrderRequest.getOrigClientOrderId(), cancelOrderRequest.getNewClientOrderId(),
                cancelOrderRequest.getRecvWindow(), cancelOrderRequest.getTimestamp());
    }

    @Override
    public List<Order> getOpenOrders(OrderRequest orderRequest) {
        return executeSync(binanceApiService.getOpenOrders(orderRequest.getSymbol(), orderRequest.getRecvWindow(), orderRequest.getTimestamp()));
    }

    @Override
    public Single<List<Order>> getOpenOrdersRx(OrderRequest orderRequest) {
        return binanceApiService.getOpenOrdersRx(orderRequest.getSymbol(), orderRequest.getRecvWindow(), orderRequest.getTimestamp());
    }

    @Override
    public List<Order> getAllOrders(AllOrdersRequest orderRequest) {
        return executeSync(binanceApiService.getAllOrders(orderRequest.getSymbol(),
                orderRequest.getOrderId(), orderRequest.getLimit(),
                orderRequest.getRecvWindow(), orderRequest.getTimestamp()));
    }

    @Override
    public Account getAccount(Long recvWindow, Long timestamp) {
        return executeSync(binanceApiService.getAccount(recvWindow, timestamp));
    }

    @Override
    public Account getAccount() {
        return getAccount(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public Single<Account> getAccountRx() {
        return binanceApiService.getAccountRx(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis())
                .retry(3);
    }

    @Override
    public List<Trade> getMyTrades(String symbol, Integer limit, Long fromId, Long recvWindow, Long timestamp) {
        return executeSync(binanceApiService.getMyTrades(symbol, limit, fromId, recvWindow, timestamp));
    }

    @Override
    public List<Trade> getMyTrades(String symbol, Integer limit) {
        return getMyTrades(symbol, limit, null, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public List<Trade> getMyTrades(String symbol) {
        return getMyTrades(symbol, null, null, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public Single<List<Trade>> getMyTradesRx(String symbol) {
        return binanceApiService.getMyTradesRx(symbol, null, null, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public void withdraw(String asset, String address, String amount, String name) {
        executeSync(binanceApiService.withdraw(asset, address, amount, name, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    @Override
    public DepositHistory getDepositHistory(String asset) {
        return executeSync(binanceApiService.getDepositHistory(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    @Override
    public Single<DepositHistory> getDepositHistoryRx(String asset) {
        return binanceApiService.getDepositHistoryRx(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public WithdrawHistory getWithdrawHistory(String asset) {
        return executeSync(binanceApiService.getWithdrawHistory(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    @Override
    public Single<WithdrawHistory> getWithdrawHistoryRx(String asset) {
        return binanceApiService.getWithdrawHistoryRx(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public DepositAddress getDepositAddress(String asset) {
        return executeSync(binanceApiService.getDepositAddress(asset, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    // User stream endpoints

    @Override
    public String startUserDataStream() {
        return executeSync(binanceApiService.startUserDataStream()).toString();
    }

    @Override
    public Single<ListenKey> startUserDataStreamRx() {
        return binanceApiService.startUserDataStreamRx();
    }

    @Override
    public void keepAliveUserDataStream(String listenKey) {
        executeSync(binanceApiService.keepAliveUserDataStream(listenKey));
    }

    @Override
    public Single<Response<Map>> keepAliveUserDataStreamRx(String listenKey) {
        return binanceApiService.keepAliveUserDataStreamRx(listenKey);
    }

    @Override
    public void closeUserDataStream(String listenKey) {
        executeSync(binanceApiService.closeAliveUserDataStream(listenKey));
    }

    @Override
    public Single<Void> closeUserDataStreamRx(String listenKey) {
        return binanceApiService.closeAliveUserDataStreamRx(listenKey);
    }
}
