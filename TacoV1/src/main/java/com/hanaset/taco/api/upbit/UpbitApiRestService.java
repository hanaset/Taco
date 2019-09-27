package com.hanaset.taco.api.upbit;

import com.hanaset.taco.api.upbit.model.UpbitAccount;
import com.hanaset.taco.api.upbit.model.UpbitMarket;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.api.upbit.model.UpbitOrderResponse;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UpbitApiRestService {

    @GET("/v1/accounts")
    Single<List<UpbitAccount>> getAccount(@Header("Authorization") String token);

    @POST("/v1/orders")
    Call<UpbitOrderResponse> createOrder(@Header("Authorization") String token, @Body UpbitOrderRequest request);

    @DELETE("/v1/order")
    Call<UpbitOrderResponse> deleteOrder(@Header("Authorization") String token, @Query("uuid") String uuid);

    @GET("/v1/market/all")
    Single<List<UpbitMarket>> getMarket();

}
