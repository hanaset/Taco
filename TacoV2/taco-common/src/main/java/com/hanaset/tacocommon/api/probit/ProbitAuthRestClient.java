package com.hanaset.tacocommon.api.probit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hanaset.tacocommon.api.probit.model.ProbitTokenBody;
import com.hanaset.tacocommon.api.probit.model.ProbitTokenResponse;
import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacocommon.model.TacoErrorCode;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Component;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Component
public class ProbitAuthRestClient {

    private final TradeKeyProperties tradeKeyProperties;
    private final TradeUrlProperties tradeUrlProperties;
    private final ProbitAuthRestSerivce probitAuthRestSerivce;

    public ProbitAuthRestClient(TradeKeyProperties tradeKeyProperties,
                               TradeUrlProperties tradeUrlProperties) {

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(tradeUrlProperties.getProbitAuthUrl())
//                .client(createOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.tradeKeyProperties = tradeKeyProperties;
        this.tradeUrlProperties = tradeUrlProperties;
        this.probitAuthRestSerivce = retrofit.create(ProbitAuthRestSerivce.class);
    }

    private String createToken() {

        String authString = tradeKeyProperties.getProbitClientId() + ":" + tradeKeyProperties.getProbitSecretKey();
        String authHeader = "Basic " + new String(Base64.getEncoder().encode(authString.getBytes()));
        return authHeader;
    }

    public String getToken() {

        String token = createToken();
        ProbitTokenBody body = ProbitTokenBody.builder().grantType("client_credentials").build();

        try {
            Response<ProbitTokenResponse> response = probitAuthRestSerivce.getToken(token, body).execute();

            if (response.isSuccessful())
                return "Bearer " + response.body().getAccessToken();

        } catch (IOException e) {
            log.error("Probit getToken() Error");
        }

        throw new TacoResponseException(TacoErrorCode.API_ERROR, "probit 토큰 발행 에러");
    }
}
