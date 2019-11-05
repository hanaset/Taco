package com.hanaset.tacocommon.api.upbit;

import com.hanaset.tacocommon.api.upbit.model.*;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UpbitApiRestService {

    @GET("/v1/accounts")
    Call<List<UpbitAccount>> getAccount(@Header("Authorization") String token);

    @POST("/v1/orders")
    Call<UpbitOrderResponse> createOrder(@Header("Authorization") String token, @Body UpbitOrderRequest request);

    @DELETE("/v1/order")
    Call<UpbitOrderResponse> deleteOrder(@Header("Authorization") String token, @Query("uuid") String uuid);

    @GET("/v1/market/all")
    Call<List<UpbitMarket>> getMarket();

    @GET("/v1/orders")
    Call<List<UpbitOrderResponse>> getOrders(@Header("Authorization") String token, @Query("market") String market);

    @GET("/v1/order")
    Call<UpbitOrderResponse> getOrder(@Header("Authorization") String token, @Query("uuid") String uuid);

}
