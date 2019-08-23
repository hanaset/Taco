package com.hanaset.taco.web.rest;

import com.hanaset.taco.api.upbit.UpbitApiRestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class UpbitAccountRest {

    private final UpbitApiRestService upbitApiRestService;

    public UpbitAccountRest(UpbitApiRestService upbitApiRestService) {
        this.upbitApiRestService = upbitApiRestService;
    }


    @GetMapping()
    public void getAccounts() {
        upbitApiRestService.getAccounts();
    }

}
