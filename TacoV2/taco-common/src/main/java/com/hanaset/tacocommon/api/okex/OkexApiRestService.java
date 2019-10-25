package com.hanaset.tacocommon.api.okex;

import com.hanaset.tacocommon.api.okex.model.OkexAccount;
import com.hanaset.tacocommon.api.okex.model.OkexOrderDetail;
import com.hanaset.tacocommon.api.okex.model.OkexOrderRequest;
import com.hanaset.tacocommon.api.okex.model.OkexOrderResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface OkexApiRestService {

    @POST("/api/spot/v3/orders")
    Call<OkexOrderResponse> orders(@Body OkexOrderRequest request);

    @GET("/api/account/v3/wallet")
    Call<List<OkexAccount>> getAccount();

    @GET("/api/spot/v3/accounts")
    Call<List<OkexAccount>> getSpotAccount();

    @POST("/api/spot/v3/cancel_orders/{order_id}")
    Call<OkexOrderResponse> cancelOrder(@Path("order_id") String orderId, @Body OkexOrderRequest request);

    @GET("/api/spot/v3/orders/{order_id}")
    Call<OkexOrderDetail> orderDetail(@Path("order_id") String orderId, @Query("instrument_id") String instrumentId);
}
