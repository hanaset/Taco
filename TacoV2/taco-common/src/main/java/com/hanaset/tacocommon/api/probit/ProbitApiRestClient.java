package com.hanaset.tacocommon.api.probit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hanaset.tacocommon.api.TacoResponse;
import com.hanaset.tacocommon.api.probit.model.*;
import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacocommon.model.TacoErrorCode;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ProbitApiRestClient {

    private final TradeUrlProperties tradeUrlProperties;
    private final ProbitApiRestService probitApiRestService;
    private final ProbitAuthRestClient probitAuthRestClient;

    public ProbitApiRestClient(TradeUrlProperties tradeUrlProperties,
                               ProbitAuthRestClient probitAuthRestClient) {

        this.tradeUrlProperties = tradeUrlProperties;

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(tradeUrlProperties.getProbitPublicUrl())
//                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.probitAuthRestClient = probitAuthRestClient;
        this.probitApiRestService = retrofit.create(ProbitApiRestService.class);
    }

    public List<ProbitMarketResponse> getMarket() {

        String token = probitAuthRestClient.getToken();

        try {
            Response<ProbitResponse<List<ProbitMarketResponse>>> response = probitApiRestService.getMarket(token).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Probit Market API Error");
            return response.body().getData();
        } catch (IOException e) {
            log.error("Probit Market API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Probit Makret API IOException");
        }
    }

    public List<ProbitBalance> getBalance() {

        String token = probitAuthRestClient.getToken();

        try {
            Response<ProbitResponse<List<ProbitBalance>>> response = probitApiRestService.getBalance(token).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Probit Balance API Error");
            return response.body().getData();
        }catch (IOException e) {
            log.error("Probit Balance API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Probit Balance API IOException");
        }
    }

    public ProbitOrderResponse order(ProbitOrderRequest request) {

        String token = probitAuthRestClient.getToken();

        try{
            Response<ProbitResponse<ProbitOrderResponse>> response = probitApiRestService.order(token, request).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Probit Order API Error");
            return response.body().getData();
        }catch (IOException e) {
            log.error("Probit Order API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Probit Order API IOException");
        }
    }

    public ProbitOrderResponse cancelOrder(ProbitOrderCancelRequest request) {

        String token = probitAuthRestClient.getToken();

        try{
            Response<ProbitResponse<ProbitOrderResponse>> response = probitApiRestService.cancelOrder(token, request).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Probit CancelOrder API Error");
            return response.body().getData();
        }catch (IOException e) {
            log.error("Probit CancelOrder API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Probit CancelOrder API IOException");
        }
    }

}
