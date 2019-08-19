package com.hanaset.taco.client;

import org.springframework.http.HttpHeaders;

import java.net.URI;

public interface AbstarctClient {

    public String getRestApi(String function);

    public URI getUri(String fuction);

    public HttpHeaders getHttpHeader();
}
