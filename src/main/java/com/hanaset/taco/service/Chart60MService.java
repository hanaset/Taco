package com.hanaset.taco.service;

import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.config.CryptoPairs;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class Chart60MService {

    private final UpbitApiRestClient upbitApiRestClient;

    public Chart60MService(UpbitApiRestClient upbitApiRestClient) {
        this.upbitApiRestClient = upbitApiRestClient;
    }

    public void getChartData() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        String date = format.format(System.currentTimeMillis());

        CryptoPairs.pairs.stream().forEach(pair->{

            String url = "candles/minutes/60?market=KRW-" + pair + "&count=24&to=" + date;
            upbitApiRestClient.getRestApi(url);
        });

    }
}
