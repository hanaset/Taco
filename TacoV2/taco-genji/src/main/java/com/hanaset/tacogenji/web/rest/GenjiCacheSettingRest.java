package com.hanaset.tacogenji.web.rest;

import com.google.common.collect.Lists;
import com.hanaset.tacocommon.cache.UpbitTransactionCached;
import com.hanaset.tacogenji.web.rest.support.GenjiApiRestSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Api(tags = "Setting API", value = "캐시 데이터에 대한 세팅")
@RestController
@RequestMapping("/setting")
public class GenjiCacheSettingRest extends GenjiApiRestSupport {

    @ApiOperation(value =
        "페어에 대한 물량을 수정"
    )
    @PostMapping("/pair")
    public ResponseEntity settingPairAmount(Double amount) {
        UpbitTransactionCached.pairAmount = BigDecimal.valueOf(amount);
        return success(UpbitTransactionCached.pairAmount.toPlainString());
    }

    @ApiOperation(value =
        "비트코인에 대한 물량을 수정"
    )
    @PostMapping("/btc")
    public ResponseEntity settingBtcAmount(Double amount) {
        UpbitTransactionCached.btcAmount = BigDecimal.valueOf(amount);
        return success(UpbitTransactionCached.btcAmount.toPlainString());
    }

    @ApiOperation(value =
        "페어와 비트코인에 대한 물량 수정"
    )
    @PostMapping()
    public ResponseEntity setting(@RequestParam Double pair, @RequestParam Double btc) {
        UpbitTransactionCached.btcAmount = BigDecimal.valueOf(btc);
        UpbitTransactionCached.pairAmount = BigDecimal.valueOf(pair);
        return success(Lists.newArrayList(UpbitTransactionCached.pairAmount, UpbitTransactionCached.btcAmount));
    }
}
