package com.hanaset.tacogenji.web.rest;

import com.hanaset.tacocommon.api.huobi.HuobiApiRestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class GenjiTestRest {

    private final HuobiApiRestClient huobiApiRestClient;

    public GenjiTestRest(HuobiApiRestClient huobiApiRestClient) {
        this.huobiApiRestClient = huobiApiRestClient;
    }

    @GetMapping("/test")
    public void test() {

        try {
            System.out.println(huobiApiRestClient.getDepth("btckrw", "step2").execute().body().toString());
            System.out.println(huobiApiRestClient.getCurreny().execute().body().toString());


            System.out.println(huobiApiRestClient.getAccount(1400795979L).execute().body().toString());
            System.out.println(huobiApiRestClient.getAccounts().execute().body().toString());
        }catch (IOException e) {

        }
    }
}
