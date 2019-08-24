package com.hanaset.taco.web.rest;

import com.hanaset.taco.service.upbit.UpbitBalanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class UpbitAccountRest {

    private final UpbitBalanceService upbitBalanceService;

    public UpbitAccountRest(UpbitBalanceService upbitBalanceService) {
        this.upbitBalanceService = upbitBalanceService;
    }


    @GetMapping()
    public String getAccounts() {
        return upbitBalanceService.getUpbitBalance().toString();
    }

}
