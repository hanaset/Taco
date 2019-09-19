package com.hanaset.taco.service.upbit;

import com.google.common.collect.Lists;
import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.UpbitAccount;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.entity.BalanceEntity;
import com.hanaset.taco.repository.BalanceRepository;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        System.out.println(upbitLists);

        for(UpbitAccount account : upbitLists) {
            if(account.getCurrency().equals(market)) {
                System.out.println(account);
                return account.getBalance();
            }
        }

        System.out.println("XXXXXXXXXXXXXXXX");
        return BigDecimal.ZERO;
    }


    public void recordBalance() {

        Single<List<UpbitAccount>> upbitLists = upbitApiRestClient.getAccount("amount");
        List<BalanceEntity> balanceEntitiyList = Lists.newArrayList();

        upbitLists.blockingGet().stream().forEach(upbitAccount -> {
            BalanceEntity entity = BalanceEntity.builder()
                    .market("upbit")
                    .asset(upbitAccount.getCurrency())
                    .amount(upbitAccount.getBalance().toPlainString())
                    .snapshot(ZonedDateTime.now())
                    .build();

            balanceEntitiyList.add(entity);
        });

        balanceRepository.saveAll(balanceEntitiyList);

    }
}
