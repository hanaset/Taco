package com.hanaset.tacogenji.web.rest;

import com.hanaset.tacocommon.cache.UpbitTransactionCached;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(tags = "Setting API", value = "캐시 데이터에 대한 세팅")
@RestController
@RequestMapping("/setting")
public class GenjiCacheSettingRest {

    @ApiOperation(value =
        "페어에 대한 물량을 수정"
    )
    @GetMapping("/pair")
    public Double settingPairAmount(Double amount) {
        UpbitTransactionCached.pairAmount = BigDecimal.valueOf(amount);

        System.out.println(UpbitTransactionCached.pairAmount);
        return amount;
    }

    @ApiOperation(value =
        "비트코인에 대한 물량을 수정"
    )
    @GetMapping("/btc")
    public Double settingBtcAmount(Double amount) {
        UpbitTransactionCached.btcAmount = BigDecimal.valueOf(amount);

        System.out.println(UpbitTransactionCached.btcAmount);
        return amount;
    }

    @ApiOperation(value =
        "페어와 비트코인에 대한 물량 수정"
    )
    @GetMapping()
    public String setting(@RequestParam Double pair, @RequestParam Double btc) {

        UpbitTransactionCached.btcAmount = BigDecimal.valueOf(btc);
        UpbitTransactionCached.pairAmount = BigDecimal.valueOf(pair);
        return "Success";
    }
}
