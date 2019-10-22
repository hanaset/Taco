package com.hanaset.tacocommon.api.huobi;

import com.hanaset.tacocommon.api.huobi.model.HuobiAccount;
import com.hanaset.tacocommon.api.huobi.model.HuobiAccounts;
import com.hanaset.tacocommon.api.huobi.model.HuobiDepth;
import com.hanaset.tacocommon.api.huobi.model.HuobiResponse;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;

import java.util.List;

@Slf4j
public class HuobiApiRestClient {

    private final TradeKeyProperties tradeKeyProperties;
    private final HuobiApiRestService huobiApiRestService;
    private final HuobiApiRestServiceGenerator huobiApiRestServiceGenerator;

    public HuobiApiRestClient(TradeKeyProperties tradeKeyProperties) {
        this.tradeKeyProperties = tradeKeyProperties;

        huobiApiRestServiceGenerator = new HuobiApiRestServiceGenerator();

        this.huobiApiRestService = huobiApiRestServiceGenerator.createService(HuobiApiRestService.class, tradeKeyProperties.getHuobiAccessKey(), tradeKeyProperties.getHuobiSecretKey());
    }

    public Call<HuobiDepth> getDepth(String symbol, String type) {
        return huobiApiRestService.getDepth(symbol, type);
    }

    public Call<HuobiResponse<List<String>>> getCurreny() {
        return huobiApiRestService.getCurrency();
    }

    public Call<HuobiAccount> getAccount(Long id) {
        return huobiApiRestService.getAccount(id);
    }

    public Call<HuobiResponse<List<HuobiAccounts>>> getAccounts() {
        return huobiApiRestService.getAccounts();
    }
}
