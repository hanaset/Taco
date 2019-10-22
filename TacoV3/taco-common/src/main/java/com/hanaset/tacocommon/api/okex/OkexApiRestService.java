package com.hanaset.tacocommon.api.okex;

import com.hanaset.tacocommon.api.okex.model.OkexBalance;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface OkexApiRestService {

    @GET("/api/account/v3/wallet")
    Call<List<OkexBalance>> getBalance();

}
