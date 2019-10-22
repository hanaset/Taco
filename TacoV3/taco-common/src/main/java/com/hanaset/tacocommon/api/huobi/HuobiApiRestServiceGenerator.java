package com.hanaset.tacocommon.api.huobi;

import com.hanaset.tacocommon.api.HttpConstants;
import com.hanaset.tacocommon.api.huobi.constants.HuobiConstants;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HuobiApiRestServiceGenerator {

    private OkHttpClient.Builder httpClient = new OkHttpClient
            .Builder()
            .connectTimeout(HttpConstants.CONNECTION_TIME_OUT, SECONDS)
            .readTimeout(HttpConstants.CONNECTION_TIME_OUT, SECONDS)
            .writeTimeout(HttpConstants.CONNECTION_TIME_OUT, SECONDS);

    private Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(HuobiConstants.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    public <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    @SuppressWarnings("Duplicates")
    public <S> S createService(Class<S> serviceClass, String apiKey, String secret) {

        Retrofit retrofit = builder.build();

        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        } catch (Exception ex) {

        }

        if (!StringUtils.isEmpty(apiKey) && !StringUtils.isEmpty(secret)) {
            HuobiSessionInterceptor interceptor = new HuobiSessionInterceptor(apiKey, secret);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
//                httpClient.addInterceptor(new LoggingInterceptor(LoggingInterceptor.LEVEL_BASIC));
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }

    public static <T> T executeSync(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw null;
        }
    }
}
