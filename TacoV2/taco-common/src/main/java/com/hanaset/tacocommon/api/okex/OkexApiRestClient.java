package com.hanaset.tacocommon.api.okex;

import com.hanaset.tacocommon.api.TacoResponse;
import com.hanaset.tacocommon.api.okex.constants.OkexApiConstants;
import com.hanaset.tacocommon.api.okex.model.OkexAccount;
import com.hanaset.tacocommon.api.okex.model.OkexOrderDetail;
import com.hanaset.tacocommon.api.okex.model.OkexOrderRequest;
import com.hanaset.tacocommon.api.okex.model.OkexOrderResponse;
import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacocommon.model.TacoErrorCode;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import com.hanaset.tacocommon.utils.DateUtils;
import com.hanaset.tacocommon.utils.HmacSHA256Base64Utils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OkexApiRestClient {

    private OkexApiRestService okexApiRestService;
    private final TradeKeyProperties tradeKeyProperties;
    private final TradeUrlProperties tradeUrlProperties;
    private Retrofit retrofit;

    public OkexApiRestClient(TradeUrlProperties tradeUrlProperties,
                             TradeKeyProperties tradeKeyProperties) {
        this.tradeUrlProperties = tradeUrlProperties;
        this.tradeKeyProperties = tradeKeyProperties;

        retrofit = new Retrofit.Builder()
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(tradeUrlProperties.getOkexPublicUrl())
                .build();

        this.okexApiRestService = retrofit.create(OkexApiRestService.class);
    }

    private OkHttpClient getClient() {
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        clientBuilder.readTimeout(30, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(30, TimeUnit.SECONDS);
        clientBuilder.retryOnConnectionFailure(true);
        clientBuilder.addInterceptor((Interceptor.Chain chain) -> {
            final Request.Builder requestBuilder = chain.request().newBuilder();
            final String timestamp = Instant.now().toString();
            requestBuilder.headers(getHeaders(chain.request(), timestamp));
            final Request request = requestBuilder.build();
            printRequest(request, timestamp);
            return chain.proceed(request);
        });
        return clientBuilder.build();
    }

    private Headers getHeaders(Request request, String timestamp) {

        Headers.Builder builder = new Headers.Builder();

        builder.add("Accept", "application/json");
        builder.add("Content-Type", "application/json; charset=UTF-8");
        if (StringUtils.isNotEmpty(tradeKeyProperties.getOkexSecretKey())) {
            builder.add("OK-ACCESS-KEY",tradeKeyProperties.getOkexAccessKey());
            builder.add("OK-ACCESS-SIGN", sign(request, timestamp));
            builder.add("OK-ACCESS-TIMESTAMP", timestamp);
            builder.add("OK-ACCESS-PASSPHRASE", tradeKeyProperties.getOkexPass());
        }
        return builder.build();

    }

    private String sign(final Request request, final String timestamp) {
        final String sign;
        try {
            sign = HmacSHA256Base64Utils.sign(timestamp, this.method(request), this.requestPath(request),
                    this.queryString(request), this.body(request), this.tradeKeyProperties.getOkexSecretKey());
        } catch (final IOException e) {
            throw new TacoResponseException(TacoErrorCode.API_ERROR,"Request get body io exception.");
        } catch (final CloneNotSupportedException e) {
            throw new TacoResponseException(TacoErrorCode.DIGEST_ERROR, "Hmac SHA256 Base64 Signature clone not supported exception.");
        } catch (final InvalidKeyException e) {
            throw new TacoResponseException(TacoErrorCode.DIGEST_ERROR, "Hmac SHA256 Base64 Signature invalid key exception.");
        }
        return sign;
    }

    private String url(final Request request) {
        return request.url().toString();
    }

    private String method(final Request request) {
        return request.method().toUpperCase();
    }

    private String requestPath(final Request request) {
        String url = this.url(request);
        url = url.replace(tradeUrlProperties.getOkexPublicUrl(), OkexApiConstants.EMPTY);
        String requestPath = url;
        if (requestPath.contains(OkexApiConstants.QUESTION)) {
            requestPath = requestPath.substring(0, url.lastIndexOf(OkexApiConstants.QUESTION));
        }
        if(tradeUrlProperties.getOkexPublicUrl().endsWith(OkexApiConstants.SLASH)){
            requestPath = OkexApiConstants.SLASH + requestPath;
        }
        return requestPath;
    }

    private String queryString(final Request request) {
        final String url = this.url(request);
        String queryString = OkexApiConstants.EMPTY;
        if (url.contains(OkexApiConstants.QUESTION)) {
            queryString = url.substring(url.lastIndexOf(OkexApiConstants.QUESTION) + 1);
        }
        return queryString;
    }

    private String body(final Request request) throws IOException {
        final RequestBody requestBody = request.body();
        String body = OkexApiConstants.EMPTY;
        if (requestBody != null) {
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            body = buffer.readString(Charset.forName("UTF-8"));
        }
        return body;
    }

    private void printRequest(final Request request, final String timestamp) {
        final String method = this.method(request);
        final String requestPath = this.requestPath(request);
        final String queryString = this.queryString(request);
        final String body;
        try {
            body = this.body(request);
        } catch (final IOException e) {
            throw new TacoResponseException(TacoErrorCode.API_ERROR, "Request get body io exception.");
        }
        final StringBuilder requestInfo = new StringBuilder();
        requestInfo.append("\n").append("\tSecret-Key: ").append(tradeKeyProperties.getOkexSecretKey());
        requestInfo.append("\n\tRequest").append("(").append(DateUtils.timeToString(null, 4)).append("):");
        requestInfo.append("\n\t\t").append("Url: ").append(this.url(request));
        requestInfo.append("\n\t\t").append("Method: ").append(method);
        requestInfo.append("\n\t\t").append("Headers: ");
        final Headers headers = request.headers();
        if (headers != null && headers.size() > 0) {
            for (final String name : headers.names()) {
                requestInfo.append("\n\t\t\t").append(name).append(": ").append(headers.get(name));
            }
        }
        requestInfo.append("\n\t\t").append("Body: ").append(body);
        final String preHash = HmacSHA256Base64Utils.preHash(timestamp, method, requestPath, queryString, body);
        requestInfo.append("\n\t\t").append("preHash: ").append(preHash);
    }



    public List<OkexAccount> getAccount() {

        try {
            Response<List<OkexAccount>> response = okexApiRestService.getAccount().execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Okex account API error");
            return response.body();
        }catch (IOException e) {
            log.error("Probit Order API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Okex account API IOException");
        }
    }

    public List<OkexAccount> getSpotAccount() {

        try {
            Response<List<OkexAccount>> response = okexApiRestService.getSpotAccount().execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Okex SpotAccount API error");
            return response.body();
        }catch (IOException e) {
            log.error("Probit Order API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Okex SpotAccount API IOException");
        }

    }

    public OkexOrderResponse order(OkexOrderRequest request) {

        try {
            Response<OkexOrderResponse> response = okexApiRestService.orders(request).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Okex order API error");
            return response.body();
        }catch (IOException e) {
            log.error("Probit Order API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Okex Order API IOException");
        }
    }

    public OkexOrderResponse cancelOrder(String orderId, OkexOrderRequest request) {

        try {
            Response<OkexOrderResponse> response = okexApiRestService.cancelOrder(orderId, request).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Okex cancelOrder API error");
            return response.body();
        }catch (IOException e) {
            log.error("Probit Order API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Okex cancelOrder API IOException");
        }
    }

    public OkexOrderDetail orderDetail(String orderId, String instrumentId) {

        try {
            Response<OkexOrderDetail> response = okexApiRestService.orderDetail(orderId, instrumentId).execute();
            TacoResponse.response(response, TacoErrorCode.API_ERROR, "Okex orderDetail API error");
            return response.body();
        }catch (IOException e) {
            log.error("Probit Order API IOException : {}", e.getMessage());
            throw new TacoResponseException(TacoErrorCode.IO_ERROR, "Okex orderDetail API IOException");
        }
    }
}
