package com.hanaset.tacocommon.api.huobi;

import com.hanaset.tacocommon.api.huobi.model.HuobiAccount;
import com.hanaset.tacocommon.api.huobi.model.HuobiAccounts;
import com.hanaset.tacocommon.api.huobi.model.HuobiDepth;
import com.hanaset.tacocommon.api.huobi.model.HuobiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface HuobiApiRestService {

    @GET("/market/depth")
    Call<HuobiDepth> getDepth(@Query("symbol") String symbol,@Query("type") String type);

    @GET("/v1/common/currencys")
    Call<HuobiResponse<List<String>>> getCurrency();

    @GET("/v1/account/accounts/{account_id}/balance")
    Call<HuobiAccount> getAccount(@Path("account_id") Long id);

    @GET("/v1/account/accounts")
    Call<HuobiResponse<List<HuobiAccounts>>> getAccounts();
}
