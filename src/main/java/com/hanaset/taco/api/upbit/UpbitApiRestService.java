package com.hanaset.taco.api.upbit;

import org.springframework.stereotype.Service;

@Service
public class UpbitApiRestService {

    private final UpbitApiRestClient upbitApiRestClient;

    public UpbitApiRestService(UpbitApiRestClient upbitApiRestClient) {
        this.upbitApiRestClient = upbitApiRestClient;
    }

    public void getAccounts() {

        upbitApiRestClient.balanceRestApi("accounts");
    }
}
