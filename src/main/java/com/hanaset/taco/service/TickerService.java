package com.hanaset.taco.service;

import com.hanaset.taco.client.trade.BithumbClient;
import com.hanaset.taco.client.trade.CoinoneClient;
import com.hanaset.taco.client.trade.UpbitClient;
import com.hanaset.taco.config.CryptoPairs;
import org.springframework.stereotype.Service;

@Service
public class TickerService {

    CryptoPairs cryptoPairs;

    private final BithumbClient bithumbClient;
    private final UpbitClient upbitClient;
    private final CoinoneClient coinoneClient;

    public TickerService(BithumbClient bithumbClient,
                         UpbitClient upbitClient,
                         CoinoneClient coinoneClient) {
        this.bithumbClient = bithumbClient;
        this.upbitClient = upbitClient;
        this.coinoneClient = coinoneClient;
    }


    public void getTicekrList() {

        cryptoPairs.pairs.stream().forEach(pair -> {
            bithumbClient.getRestApi("ticker/" + pair);
            upbitClient.getRestApi("ticker?markets=KRW-" + pair);
            coinoneClient.getRestApi("ticker?currency=" + pair);
        });
    }

}
