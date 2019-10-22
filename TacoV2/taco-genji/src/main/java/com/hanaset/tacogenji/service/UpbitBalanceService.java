package com.hanaset.tacogenji.service;

import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.api.upbit.model.UpbitAccount;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderRequest;
import com.hanaset.tacocommon.api.upbit.model.UpbitOrderResponse;
import com.hanaset.tacocommon.cache.UpbitTransactionCached;
import com.hanaset.tacocommon.entity.BalanceEntity;
import com.hanaset.tacocommon.repository.BalanceRepository;
import io.reactivex.Single;
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

    public List<UpbitAccount> getUpbitBalance() {

        Single<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount");

        return upbitLists.blockingGet();
    }

    public BigDecimal getUpbitMarketAccount(String market) {

        List<UpbitAccount> upbitLists = upbitApiRestClient.getAccount("amount").blockingGet();

        for (UpbitAccount account : upbitLists) {
            if (account.getCurrency().equals(market)) {
                return account.getBalance();
            }
        }

        return BigDecimal.ZERO;
    }

    public void cacheSaveBalance() {

        List<UpbitAccount> upbitLists = upbitApiRestClient.getAccount("amount").blockingGet();

        for (UpbitAccount account : upbitLists) {
            if(account.getCurrency().equals("BTC")) {
                UpbitTransactionCached.btcAmount = account.getBalance();
            }else if(!account.getCurrency().equals("KRW") && !account.getCurrency().equals("BTC")) {
                UpbitTransactionCached.pairAmount = account.getBalance();
            }
        }
    }

    public void initBalance() {

        List<UpbitAccount> upbitLists = upbitApiRestClient.getAccount("amount").blockingGet();

        upbitLists.forEach(upbitAccount -> {
            if(!upbitAccount.getCurrency().equals("KRW")) {
                try {
                    askingMarket(upbitAccount.getBalance(), "KRW-" + upbitAccount.getCurrency());
                }catch (IOException e) {
                    log.error("init balance() IOException Error : {}", e.getMessage());
                }
            }
        });

    }

    public void startBalance(String pair) {

        List<UpbitAccount> upbitLists = upbitApiRestClient.getAccount("amount").blockingGet();

        try {
            for (UpbitAccount account : upbitLists) {
                if (account.getCurrency().equals("KRW")) {
                    balanceRepository.save(BalanceEntity.builder().amount(account.getBalance()).build());
                    bidingMarket(account.getBalance().divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1000)), "KRW-" + pair);
                    bidingMarket(account.getBalance().divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).add(BigDecimal.valueOf(500)), "KRW-BTC");
                }
            }

            Thread.sleep(1000 * 3);
            cacheSaveBalance();
        }catch (IOException e) {
            log.error("startBalance Error : {}", e.getMessage());
        }catch (InterruptedException e) {
            log.error("startBalance Error : {}", e.getMessage());
        }
    }

    private List saveBalance() {
        List<UpbitAccount> upbitAccounts = upbitApiRestClient.getAccount("amount").blockingGet();
        return upbitAccounts.stream().filter(upbitAccount -> upbitAccount.getCurrency().equals("KRW"))
                .map(upbitAccount -> balanceRepository.save(BalanceEntity.builder().amount(upbitAccount.getBalance()).build()))
                .collect(Collectors.toList());
    }

    private Response<UpbitOrderResponse> bidingMarket(BigDecimal price, String pair) throws IOException {

        // 매수
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("bid")
                .price(price.toPlainString())
                .volume(null)
                .ord_type("price")
                .build();

        System.out.println(request);

        Response response = upbitApiRestClient.bidOrder(request).execute();

        if(response.isSuccessful()) {
            log.info("PAIR : {} 구매 완료 [ {} ]", pair, response.body());
        }else {
            log.error("PAIR : {} 구매 실패 [ {} ]", pair, response.errorBody().byteString());
        }

        return response;
    }

    private Response<UpbitOrderResponse> askingMarket(BigDecimal amount, String pair) throws IOException  {

        // 매도
        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market(pair)
                .side("ask")
                .price(null)
                .volume(amount.toPlainString())
                .ord_type("market")
                .build();

        System.out.println(request);

        Response response = upbitApiRestClient.askOrder(request).execute();

        if(response.isSuccessful()) {
            log.info("PAIR : {} 판매 완료 [ {} ]", pair, response.body());
        }else {
            log.error("PAIR : {} 판매 실패 [ {} ]", pair, response.errorBody().byteString());
        }

        return response;
    }

    public void exchangeResult(String type) {
        BigDecimal btcAmount = getUpbitMarketAccount("BTC");
        try {
            if (type.equals("KRW")) { // PAIR를 원화로 매도하고 BTC로 매수한 경우
                bidingMarket(btcAmount.subtract(UpbitTransactionCached.btcAmount), "KRW-BTC");
            } else if (type.equals("BTC")) { // 반대
                askingMarket(UpbitTransactionCached.btcAmount.subtract(btcAmount), "KRW-BTC");
            }
        }catch (IOException e) {
            log.error("exchangeResult Error : {}", e.getMessage());
        }
    }
}
