package com.hanaset.tacogenji.service;

import com.hanaset.tacocommon.api.TacoResponse;
import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.api.upbit.model.UpbitAccount;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderRequest;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderResponse;
import com.hanaset.tacocommon.cache.UpbitTransactionCached;
import com.hanaset.tacocommon.entity.BalanceEntity;
import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacocommon.model.TacoErrorCode;
import com.hanaset.tacocommon.repository.BalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UpbitBalanceService {

    private final UpbitApiRestClient upbitApiRestClient;
    private final BalanceRepository balanceRepository;

    public UpbitBalanceService(UpbitApiRestClient upbitApiRestClient,
                               BalanceRepository balanceRepository) {
        this.upbitApiRestClient = upbitApiRestClient;
        this.balanceRepository = balanceRepository;
    }

//    public Response<List<UpbitAccount>> getUpbitBalance() throws IOException{
//
//        Response<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount").execute();
//
//        return upbitLists;
//    }
//
//    public BigDecimal getUpbitMarketAccount(String market) throws IOException{
//
//        Response<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount").execute();
//
//        for (UpbitAccount account : upbitLists) {
//            if (account.getCurrency().equals(market)) {
//                return account.getBalance();
//            }
//        }
//
//        return BigDecimal.ZERO;
//    }

    public void cacheSaveBalance() {

        try {
            Response<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount").execute();

            TacoResponse.response(upbitLists, TacoErrorCode.API_ERROR, "업비트 API 통신 에러");

            upbitLists.body().stream().forEach(upbitAccount -> {

                switch (upbitAccount.getCurrency()) {
                    case "BTC":
                        UpbitTransactionCached.btcAmount = upbitAccount.getBalance();
                        break;
                    case "KRW":
                        break;
                    default:
                        UpbitTransactionCached.pairAmount = upbitAccount.getBalance();
                        break;
                }
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void initBalance() {

        try {

            Response<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount").execute();

            TacoResponse.response(upbitLists, TacoErrorCode.API_ERROR, "업비트 API 통신 에러");

            upbitLists.body().stream().filter(upbitAccount -> !upbitAccount.getCurrency().equals("KRW"))
                    .forEach(upbitAccount -> askingMarket(upbitAccount.getBalance(), "KRW-" + upbitAccount.getCurrency()));

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public void startBalance(String pair) {

        try {

            Response<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount").execute();

            TacoResponse.response(upbitLists, TacoErrorCode.API_ERROR, "업비트 API 통신 에러");

            upbitLists.body().stream().filter(upbitAccount -> upbitAccount.getCurrency().equals("KRW"))
                    .forEach(upbitAccount -> {
                        balanceRepository.save(BalanceEntity.builder().amount(upbitAccount.getBalance()).build());
                        bidingMarket(upbitAccount.getBalance().divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1000)), "KRW-" + pair);
                        bidingMarket(upbitAccount.getBalance().divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).add(BigDecimal.valueOf(500)), "KRW-BTC");
                    });

            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                log.error("Start Balance Error : {}", e.getMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private List saveBalance() {

        try {

            Response<List<UpbitAccount>> upbitAccounts = upbitApiRestClient.getAccount("amount").execute();

            TacoResponse.response(upbitAccounts, TacoErrorCode.API_ERROR, "업비트 API 통신 에러");

            return upbitAccounts.body().stream().filter(upbitAccount -> upbitAccount.getCurrency().equals("KRW"))
                    .map(upbitAccount -> balanceRepository.save(BalanceEntity.builder().amount(upbitAccount.getBalance()).build()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "IOException");
        }
    }

    private Response<UpbitOrderResponse> bidingMarket(BigDecimal price, String pair) {

        // 매수
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("bid")
                .price(price.toPlainString())
                .volume(null)
                .ord_type("price")
                .build();

        System.out.println(request);

        try {
            Response response = upbitApiRestClient.bidOrder(request).execute();

            if (response.isSuccessful()) {
                log.info("PAIR : {} 구매 완료 [ {} ]", pair, response.body());
            } else {
                log.error("PAIR : {} 구매 실패 [ {} ]", pair, response.errorBody().byteString());
            }
            return response;
        } catch (IOException e) {
            throw new TacoResponseException(TacoErrorCode.API_ERROR, "BID 에러");
        }


    }

    private Response<UpbitOrderResponse> askingMarket(BigDecimal amount, String pair) {

        // 매도
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("ask")
                .price(null)
                .volume(amount.toPlainString())
                .ord_type("market")
                .build();

        System.out.println(request);

        try {
            Response response = upbitApiRestClient.askOrder(request).execute();

            if (response.isSuccessful()) {
                log.info("PAIR : {} 판매 완료 [ {} ]", pair, response.body());
            } else {
                log.error("PAIR : {} 판매 실패 [ {} ]", pair, response.errorBody().byteString());
            }

            return response;

        } catch (IOException e) {
            throw new TacoResponseException(TacoErrorCode.API_ERROR, "ASK 에러");
        }
    }

//    public void exchangeResult(String type) {
//        BigDecimal btcAmount = getUpbitMarketAccount("BTC");
//        try {
//            if (type.equals("KRW")) { // PAIR를 원화로 매도하고 BTC로 매수한 경우
//                bidingMarket(btcAmount.subtract(UpbitTransactionCached.btcAmount), "KRW-BTC");
//            } else if (type.equals("BTC")) { // 반대
//                askingMarket(UpbitTransactionCached.btcAmount.subtract(btcAmount), "KRW-BTC");
//            }
//        } catch (IOException e) {
//            log.error("exchangeResult Error : {}", e.getMessage());
//        }
//    }
}
