package com.hanaset.tacoreaper.web;

import com.hanaset.tacocommon.api.probit.ProbitApiRestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class ReaperTest {

    private final ProbitApiRestClient probitApiRestClient;

    public ReaperTest(ProbitApiRestClient probitApiRestClient) {
        this.probitApiRestClient = probitApiRestClient;
    }

    @GetMapping()
    public void test() {
        probitApiRestClient.getToken();
    }
}
