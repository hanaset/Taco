package com.hanaset.tacomercy.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.api.upbit.model.UpbitMarket;
import com.hanaset.tacocommon.utils.Taco2UpbitConvert;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UpbitMercyMarketService {

    private final UpbitApiRestClient upbitApiRestClient;
    private List<String> pairs = Lists.newArrayList();

    public UpbitMercyMarketService(UpbitApiRestClient upbitApiRestClient) {
        this.upbitApiRestClient = upbitApiRestClient;
    }

    private void setPairs() {

        List<UpbitMarket> marketList = upbitApiRestClient.getMarket().blockingGet();
        Map<String, Integer> pairMap = Maps.newHashMap();

        for(UpbitMarket market : marketList) {
            if(market.getMarket().contains("KRW") || market.getMarket().contains("BTC")) {

                String key = Taco2UpbitConvert.convertPair(market.getMarket());
                if(pairMap.containsKey(key)) {
                    pairMap.replace(key, pairMap.get(key)+1);
                }else {
                    pairMap.put(key, 1);
                }
            }
        }

        pairMap.forEach((k, v) ->{
            if(v == 2) {
                pairs.add("KRW-"+k);
                pairs.add("BTC-"+k);
            }
        });

        pairs.add("KRW-BTC");
    }

    public List initPairs() {
        setPairs();
        return getPairs();
    }

    public List getPairs() {
        return pairs;
    }
}