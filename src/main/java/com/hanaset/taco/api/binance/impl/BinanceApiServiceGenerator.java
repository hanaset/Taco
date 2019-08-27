package com.hanaset.taco.api.binance.impl;

import com.google.gson.Gson;
import com.hanaset.taco.api.binance.BinanceApiError;
import com.hanaset.taco.api.binance.HttpConstants;
import com.hanaset.taco.api.binance.constant.BinanceApiConstants;
import com.hanaset.taco.api.binance.exception.BinanceApiException;
import com.hanaset.taco.api.binance.security.AuthenticationInterceptor;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Generates a Binance API implementation based on @see {@link BinanceApiService}.
 */
public class BinanceApiServiceGenerator {

    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(HttpConstants.CONNECTION_TIME_OUT, SECONDS)
            .readTimeout(HttpConstants.CONNECTION_TIME_OUT, SECONDS)
            .writeTimeout(HttpConstants.CONNECTION_TIME_OUT, SECONDS);

    private Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BinanceApiConstants.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    public <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public <S> S createService(Class<S> serviceClass, String apiKey, String secret) {

        Retrofit retrofit = builder.build();

        if (!StringUtils.isEmpty(apiKey) && !StringUtils.isEmpty(secret)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(apiKey, secret);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
//                httpClient.addInterceptor(new LoggingInterceptor(LoggingInterceptor.LEVEL_BASIC));
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }

    /**
     * Execute a REST call and block until the response is received.
     */
    public static <T> T executeSync(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                BinanceApiError apiError = getBinanceApiError(response);
                throw new BinanceApiException(apiError);
            }
        } catch (IOException e) {
            throw new BinanceApiException(e);
        }
    }

    /**
     * Extracts and converts the response error body into an object.
     */
    public static BinanceApiError getBinanceApiError(Response<?> response) throws IOException, BinanceApiException {

        String url = response.raw().request().url().toString();
        int code = response.code();
        byte[] responseBody = readBody(response.errorBody().byteStream());
        String body = new String(responseBody, 0, responseBody.length);

        BinanceApiError binanceApiError = new Gson().fromJson(body, BinanceApiError.class);

        return binanceApiError;

//        return (BinanceApiError) retrofit.responseBodyConverter(BinanceApiError.class, new Annotation[0])
//                .convert(response.errorBody());
    }

    private static byte[] readBody(InputStream is) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            int nLength = 0;
            while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            return baos.toByteArray();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
    }
}