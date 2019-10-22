package com.hanaset.tacocommon.api.huobi;

import com.google.common.collect.Lists;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class HuobiSessionInterceptor implements Interceptor {

    private MediaType DEFAULT_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");

    private final String apiKey;
    private final String apiSecret;

    public HuobiSessionInterceptor(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        HttpUrl httpUrl = request.url().newBuilder().build();

        if (StringUtils.isNoneEmpty(apiKey) && StringUtils.isNoneEmpty(apiSecret)) {

            String httpMethod = request.method();
            String host = httpUrl.host();
            String path = httpUrl.encodedPath();
            String nonce = createUTCDate();

            httpUrl = httpUrl.newBuilder()
                    .addQueryParameter("AccessKeyId", apiKey)
                    .addQueryParameter("SignatureMethod", "HmacSHA256")
                    .addQueryParameter("SignatureVersion", "2")
                    .addQueryParameter("Timestamp", nonce)
                    .build();

            final HttpUrl finalHttpUrl = httpUrl;
            String query = Lists.newArrayList(httpUrl.queryParameterNames())
                    .stream()
                    .sorted(String::compareTo)
                    .map(key -> key + "=" + encodeValue(finalHttpUrl.queryParameter(key)))
                    .collect(Collectors.joining("&"));


            System.out.println("httpMethod : " + httpMethod);
            System.out.println("host : " + host);
            System.out.println("path : " + path);
            System.out.println("query : " + query);

            String toSign = String.format("%s\n%s\n%s\n%s", httpMethod, host, path, query);

            HuobiDigest huobiDigest = HuobiDigest.createInstance(this.apiSecret);
            String signature = huobiDigest.getDigest(toSign);

            if (StringUtils.isNoneEmpty(signature)) {
                httpUrl = httpUrl.newBuilder()
                        .addQueryParameter("Signature", signature)
                        .build();
            }
        }

        System.out.println("query : " + httpUrl.encodedQuery());

        Request.Builder builder = request
                .newBuilder()
                .url(httpUrl);

        request = builder.build();

        return chain.proceed(request);
    }

    private String createUTCDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    private String encodeValue(String value) {
        String ret;
        try {
            ret = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return ret;
    }
}

