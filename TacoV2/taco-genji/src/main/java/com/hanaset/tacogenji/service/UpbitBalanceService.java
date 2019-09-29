package com.hanaset.tacogenji.service;

import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.api.upbit.model.UpbitAccount;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class UpbitBalanceService {

    private final UpbitApiRestClient upbitApiRestClient;

    public UpbitBalanceService(UpbitApiRestClient upbitApiRestClient) {
        this.upbitApiRestClient = upbitApiRestClient;
    }

    public List<UpbitAccount> getUpbitBalance() {

        Single<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount");

        return upbitLists.blockingGet();
    }

    public BigDecimal getUpbitMarketAccount(String market) {

        List<UpbitAccount> upbitLists = upbitApiRestClient.getAccount("amount").blockingGet();

        //System.out.println(upbitLists);

        for(UpbitAccount account : upbitLists) {
            if(account.getCurrency().equals(market)) {
                //System.out.println(account);
                return account.getBalance();
            }
        }

        return BigDecimal.ZERO;
    }
}
