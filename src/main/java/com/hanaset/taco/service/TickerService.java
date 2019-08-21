package com.hanaset.taco.service;

import com.hanaset.taco.api.bithumb.BithumbRestClient;
import com.hanaset.taco.api.coinone.CoinoneRestClient;
import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.config.CryptoPairs;
import org.springframework.stereotype.Service;

@Service
public class TickerService {

    private final BithumbRestClient bithumbRestClient;
    private final UpbitApiRestClient upbitApiRestClient;
    private final CoinoneRestClient coinoneRestClient;

    public TickerService(BithumbRestClient bithumbRestClient,
                         UpbitApiRestClient upbitApiRestClient,
                         CoinoneRestClient coinoneRestClient) {
        this.bithumbRestClient = bithumbRestClient;
        this.upbitApiRestClient = upbitApiRestClient;
        this.coinoneRestClient = coinoneRestClient;
    }


    public void getTicekrList() {

        CryptoPairs.pairs.stream().forEach(pair -> {
            bithumbRestClient.getRestApi("ticker/" + pair);
            upbitApiRestClient.getRestApi("ticker?markets=KRW-" + pair);
            coinoneRestClient.getRestApi("ticker?currency=" + pair);
        });
    }

}
