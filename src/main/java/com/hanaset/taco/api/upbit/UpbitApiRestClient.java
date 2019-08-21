package com.hanaset.taco.api.upbit;

import com.hanaset.taco.client.AbstarctClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@AllArgsConstructor
public class UpbitApiRestClient implements AbstarctClient {

    private String publicUrl;
    private RestTemplate restTemplate;

    public String getRestApi(String function) {

        try {
            String response = restTemplate.getForObject(getUri(function), String.class);
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
        return httpHeaders;
    }
}
