package com.hanaset.tacocommon.api.probit;

import com.hanaset.tacocommon.api.probit.model.ProbitTokenBody;
import com.hanaset.tacocommon.api.probit.model.ProbitTokenResponse;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import org.springframework.stereotype.Component;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Base64;

@Component
public class ProbitApiRestClient {

    private final TradeKeyProperties tradeKeyProperties;
    private final TradeUrlProperties tradeUrlProperties;
    private final ProbitApiRestService probitApiRestService;

    public ProbitApiRestClient(TradeKeyProperties tradeKeyProperties,
                               TradeUrlProperties tradeUrlProperties) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://accounts.probit.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.tradeKeyProperties = tradeKeyProperties;
        this.tradeUrlProperties = tradeUrlProperties;
        this.probitApiRestService = retrofit.create(ProbitApiRestService.class);
    }

    private String createToken() {

        String authString = tradeKeyProperties.getProbitClientId() + ":" + tradeKeyProperties.getProbitSecretKey();
        String authHeader = "Basic " + new String(Base64.getEncoder().encode(authString.getBytes()));
        return authHeader;
    }

    public void getToken() {

        String token = createToken();
        ProbitTokenBody body = ProbitTokenBody.builder().grantType("client_credentials").build();

        try {
            Response<ProbitTokenResponse> response = probitApiRestService.getToken(token, body).execute();

            if (response.isSuccessful()) {
                System.out.println(response.body().toString());
            } else {
                System.out.println("error : " + response.errorBody().byteString().toString());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
