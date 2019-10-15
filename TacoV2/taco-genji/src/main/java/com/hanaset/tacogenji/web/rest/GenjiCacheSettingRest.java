package com.hanaset.tacogenji.web.rest;

import com.hanaset.tacocommon.cache.UpbitTransactionCached;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/setting")
public class GenjiCacheSettingRest {

    @GetMapping("/pair")
    public Double settingPairAmount(Double amount) {
        UpbitTransactionCached.pairAmount = BigDecimal.valueOf(amount);

        System.out.println(UpbitTransactionCached.pairAmount);
        return amount;
    }

    @GetMapping("/btc")
    public Double settingBtcAmount(Double amount) {
        UpbitTransactionCached.btcAmount = BigDecimal.valueOf(amount);

        System.out.println(UpbitTransactionCached.btcAmount);
        return amount;
    }

    @GetMapping()
    public String setting(@RequestParam Double pair, @RequestParam Double btc) {

        UpbitTransactionCached.btcAmount = BigDecimal.valueOf(btc);
        UpbitTransactionCached.pairAmount = BigDecimal.valueOf(pair);
        return "Success";
    }
}
