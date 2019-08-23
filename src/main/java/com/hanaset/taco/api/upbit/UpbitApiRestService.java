package com.hanaset.taco.api.upbit;

import com.hanaset.taco.api.upbit.model.UpbitAccount;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.api.upbit.model.UpbitOrderResponse;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.List;

public interface UpbitApiRestService {

    @GET("/v1/accounts")
    Single<List<UpbitAccount>> getAccount(@Header("Authorization") String token);

    @POST("/v1/orders")
    Single<UpbitOrderResponse> createOrder(@Header("Authorization") String token, @Body UpbitOrderRequest request);


}
