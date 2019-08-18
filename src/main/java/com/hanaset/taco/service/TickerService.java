package com.hanaset.taco.service;

import com.hanaset.taco.client.trade.BithumbClient;
import com.hanaset.taco.client.trade.UpbitClient;
import org.springframework.stereotype.Service;

@Service
public class TickerService {

    private final BithumbClient bithumbClient;
    private final UpbitClient upbitClient;

    public TickerService(BithumbClient bithumbClient,
                         UpbitClient upbitClient) {
        this.bithumbClient = bithumbClient;
        this.upbitClient = upbitClient;
    }


    public void getTicekrList(String pair) {

        bithumbClient.getRestApi("ticker/all");
        upbitClient.getRestApi("market/all"); // upbit는 전체보기가 없음

    }

}
