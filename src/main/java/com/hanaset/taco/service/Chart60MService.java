package com.hanaset.taco.service;

import com.hanaset.taco.client.trade.UpbitClient;
import com.hanaset.taco.config.CryptoPairs;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Set;

@Service
public class Chart60MService {

    CryptoPairs cryptoPairs;

    private final UpbitClient upbitClient;

    public Chart60MService(UpbitClient upbitClient) {
        this.upbitClient = upbitClient;
    }

    public void getChartData() {

        Set<String> pairs = cryptoPairs.pairs;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        String date = format.format(System.currentTimeMillis());

        pairs.stream().forEach(pair->{

            String url = "candles/minutes/60?market=KRW-" + pair + "&count=24&to=" + date;
            upbitClient.getRestApi(url);
        });

    }
}
