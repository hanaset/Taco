package com.hanaset.tacomercy.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hanaset.tacocommon.api.TacoResponse;
import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.api.upbit.model.UpbitMarket;
import com.hanaset.tacocommon.model.TacoErrorCode;
import com.hanaset.tacocommon.utils.Taco2UpbitConvert;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UpbitMercyMarketService {

    private final UpbitApiRestClient upbitApiRestClient;
    private List<String> pairs = Lists.newArrayList();

    public UpbitMercyMarketService(UpbitApiRestClient upbitApiRestClient) {
        this.upbitApiRestClient = upbitApiRestClient;
    }

    private void setPairs() {

        try {
            Response<List<UpbitMarket>> marketList = upbitApiRestClient.getMarket().execute();

            TacoResponse.response(marketList, TacoErrorCode.API_ERROR, "UPBIT API ERROR");
            Map<String, Integer> pairMap = marketList.body().stream().filter(upbitMarket -> upbitMarket.getMarket().contains("KRW") || upbitMarket.getMarket().contains("BTC"))
                    .collect(Collectors.toMap(upbitMarket -> Taco2UpbitConvert.convertPair(upbitMarket.getMarket()), upbitMarket -> 1, (v1, v2) -> v1 + v2));


            pairMap.forEach((k, v) -> {
                if (v == 2) {
                    pairs.add("KRW-" + k);
                    pairs.add("BTC-" + k);
                }
            });

            pairs.add("KRW-BTC");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List initPairs() {
        setPairs();
        return getPairs();
    }

    public List getPairs() {
        return pairs;
    }
}