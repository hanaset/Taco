package com.hanaset.taco.api.upbit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.hanaset.taco.api.upbit.model.UpbitAccount;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.api.upbit.model.UpbitOrderResponse;
import com.hanaset.taco.properties.TradeKeyProperties;
import com.hanaset.taco.properties.TradeUrlProperties;
import com.hanaset.taco.utils.HashConvert;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.UUID;

@Slf4j
public class UpbitApiRestClient {

    private final TradeKeyProperties tradeKeyProperties;
    private final TradeUrlProperties tradeUrlProperties;
    private final UpbitApiRestService upbitApiRestService;

    public UpbitApiRestClient(TradeKeyProperties tradeKeyProperties,
                              TradeUrlProperties tradeUrlProperties) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(tradeUrlProperties.getUpbitPublicUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.tradeKeyProperties = tradeKeyProperties;
        this.tradeUrlProperties = tradeUrlProperties;
        this.upbitApiRestService = retrofit.create(UpbitApiRestService.class);
    }


    public Single<List<UpbitAccount>> getAccount(String query) {
        return upbitApiRestService.getAccount(createToken(query));
    }

    public Call<UpbitOrderResponse> createOrder(UpbitOrderRequest request) {

        String query = "market=" + request.getMarket()
                + "&side=" + request.getSide()
                + "&volume=" + request.getVolume()
                + "&price=" + request.getPrice()
                + "&ord_type=" + request.getOrd_type();

        return upbitApiRestService.createOrder(createToken(query), request);
    }

    public Call<UpbitOrderResponse> deleteOrder(String uuid) {

        String query = "uuid=" + uuid;

        return upbitApiRestService.deleteOrder(createToken(query), uuid);
    }

    private String createToken(String queryString) {

        String token = null;

        try {

            JWTCreator.Builder jwtBulider = JWT.create();

            jwtBulider
                    .withClaim("access_key", tradeKeyProperties.getUpbitAccessKey())
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("query", queryString);

            if (!StringUtils.isEmpty(queryString)) {
                jwtBulider.withClaim("query", queryString);
            }

            token = jwtBulider.sign(Algorithm.HMAC256(tradeKeyProperties.getUpbitSecretKey()));
        } catch (Exception e) {
            log.error("[upbit] Auth Error -> {}", e.getMessage());
        }

        return "Bearer " + (StringUtils.isEmpty(token) ? "" : token);
    }

    public Call<UpbitOrderResponse> askOrder(UpbitOrderRequest request) {

        String query = "market=" + request.getMarket()
                + "&side=" + request.getSide()
                + "&volume=" + request.getVolume()
                + "&ord_type=" + request.getOrd_type();

        return upbitApiRestService.createOrder(createToken(query), request);
    }

    public Call<UpbitOrderResponse> bidOrder(UpbitOrderRequest request) {

        String query = "market=" + request.getMarket()
                + "&side=" + request.getSide()
                + "&price=" + request.getPrice()
                + "&ord_type=" + request.getOrd_type();

        return upbitApiRestService.createOrder(createToken(query), request);
    }




}
