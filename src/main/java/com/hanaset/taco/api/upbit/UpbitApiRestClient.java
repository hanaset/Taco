package com.hanaset.taco.api.upbit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaset.taco.api.upbit.model.UpbitAccount;
import com.hanaset.taco.client.AbstarctClient;
import com.hanaset.taco.properties.TradeKeyProperties;
import com.hanaset.taco.utils.HashConvert;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class UpbitApiRestClient implements AbstarctClient {

    private String publicUrl;
    private RestTemplate restTemplate;
    private final TradeKeyProperties tradeKeyProperties;

    public UpbitApiRestClient(TradeKeyProperties tradeKeyProperties) {
        this.tradeKeyProperties = tradeKeyProperties;
    }

    public String getRestApi(String function) {

        try {
            String response = restTemplate.getForObject(getUri(function), String.class);

            System.out.println(createToken(null));
            //log.info(response);
            return response;
        } catch (HttpClientErrorException e) {
            log.error("[upbit] -> {}", e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("[upbit] -> {}", e.getMessage());
        }

        return null;
    }

    public URI getUri(String fuction) {
        String url = publicUrl + fuction;
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);

        //System.out.println(url);
        return uriComponentsBuilder.build().encode().toUri();
    }

    public HttpHeaders getHttpHeader() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, createToken(""));
        return httpHeaders;
    }

    private String createToken(String queryString) {

        String token = null;

        try {

            JWTCreator.Builder jwtBulider = JWT.create();

            jwtBulider
                    .withClaim("access_key", tradeKeyProperties.getUpbitAccessKey())
                    .withClaim("nonce", System.currentTimeMillis())
                    .withClaim("query", queryString);
                    //.withClaim("query_hash", HashConvert.getSHA512(""));

            token = jwtBulider.sign(Algorithm.HMAC256(tradeKeyProperties.getUpbitSecretKey()));
        } catch (Exception e) {
            log.error("[upbit] Auth Error -> {}", e.getMessage());
        }

        return "Bearer " + (StringUtils.isEmpty(token) ? "" : token);
    }

    public String balanceRestApi(String function) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpEntity<String> httpEntity = new HttpEntity<>(null, getHttpHeader());
            ResponseEntity<List<UpbitAccount>> response = restTemplate.exchange(getUri(function), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<UpbitAccount>>() {});
            log.info(response.getBody().toString());
        } catch (HttpClientErrorException e) {
            log.error("[upbit] -> {}", e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("[upbit] -> {}", e.getMessage());
        }

        return null;
    }
}
