package com.hanaset.tacocommon.api.okex;

import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class OkexApiRestClient {

    private final TradeUrlProperties tradeUrlProperties;
    private final TradeKeyProperties tradeKeyProperties;
    private final OkexApiRestService okexApiRestService;

    public OkexApiRestClient(TradeKeyProperties tradeKeyProperties,
                             TradeUrlProperties tradeUrlProperties) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(tradeUrlProperties.getUpbitPublicUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.tradeKeyProperties = tradeKeyProperties;
        this.tradeUrlProperties = tradeUrlProperties;
        this.okexApiRestService = retrofit.create(OkexApiRestService.class);
    }
}
