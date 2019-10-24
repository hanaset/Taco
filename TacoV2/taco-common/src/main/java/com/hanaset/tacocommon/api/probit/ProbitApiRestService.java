package com.hanaset.tacocommon.api.probit;

import com.hanaset.tacocommon.api.probit.model.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.List;

public interface ProbitApiRestService {

    @GET("/api/exchange/v1/market")
    Call<ProbitResponse<List<ProbitMarketResponse>>> getMarket(@Header("Authorization") String auth);

    @POST("/api/exchange/v1/new_order")
    Call<ProbitResponse<ProbitOrderResponse>> order(@Header("Authorization") String auth, @Body ProbitOrderRequest request);

    @POST("/api/exchange/v1/cancel_order")
    Call<ProbitResponse<ProbitOrderResponse>> cancelOrder(@Header("Authorization") String auth, @Body ProbitOrderCancelRequest request);

    @GET("/api/exchange/v1/balance")
    Call<ProbitResponse<List<ProbitBalance>>> getBalance(@Header("Authorization") String auth);
}
