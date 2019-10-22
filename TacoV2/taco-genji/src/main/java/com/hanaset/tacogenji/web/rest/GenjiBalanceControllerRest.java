package com.hanaset.tacogenji.web.rest;

import com.hanaset.tacocommon.utils.DateTimeUtils;
import com.hanaset.tacogenji.api.upbit.UpbitGenjiWebSocketService;
import com.hanaset.tacogenji.service.CryptoSelectService;
import com.hanaset.tacogenji.service.UpbitBalanceService;
import com.hanaset.tacogenji.web.rest.support.GenjiApiRestSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Controller API", value = "데이터 설정")
@RestController
@RequestMapping("/control")
public class GenjiBalanceControllerRest extends GenjiApiRestSupport {

    private final UpbitBalanceService upbitBalanceService;
    private final CryptoSelectService cryptoSelectService;
    private final UpbitGenjiWebSocketService upbitGenjiWebSocketService;

    public GenjiBalanceControllerRest(UpbitBalanceService upbitBalanceService,
                                      CryptoSelectService cryptoSelectService,
                                      UpbitGenjiWebSocketService upbitGenjiWebSocketService) {
        this.upbitBalanceService = upbitBalanceService;
        this.cryptoSelectService = cryptoSelectService;
        this.upbitGenjiWebSocketService = upbitGenjiWebSocketService;
    }

    @ApiOperation(value =
            "자산 초기화 = 현재 구매되어 있는 모든 페어를 팔고 원화로 전환"
    )
    @GetMapping("/init")
    public ResponseEntity init() {
        upbitGenjiWebSocketService.orderbookDisconnect();
        upbitBalanceService.initBalance();
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @ApiOperation(value =
            "로직 시작 = 이틀동안 가장 많은 이윤을 남긴 페어를 선택하여 원화 1/3, 비트코인 1/3, 페어 1/3를 구매 후 로직 시작"
    )
    @GetMapping("/start")
    public ResponseEntity start() {
        String pair = cryptoSelectService.getPair(DateTimeUtils.getCurrentBeforeNDay("yyyy-MM-dd", "Asia/Seoul", 2), DateTimeUtils.getCurrentDay("Asia/Seoul"));
        upbitBalanceService.startBalance(pair);
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @ApiOperation(value =
        "로직 시작 = 하루동안 가장 많은 이윤을 남긴 페어를 선탁하여 원화 1/3, 비트코인 1/3, 페어 1/3를 구매 후 로직 시작"
    )
    @GetMapping("/current_start")
    public ResponseEntity currentStart() {
        String pair = cryptoSelectService.getCurrentPair(DateTimeUtils.getCurrentDay("Asia/Seoul"));
        upbitBalanceService.startBalance(pair);
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @ApiOperation(value =
        "페어 선택 = 직접 페어를 선택하여 로직을 시작"
    )
    @GetMapping("/pair_start")
    public ResponseEntity pairStart(@RequestParam String pair) {
        upbitBalanceService.startBalance(pair);
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }


    @ApiOperation(value =
        "오더북 연결 = 해당 페어에 대해 오더북을 연결시킨다."
    )
    @GetMapping("/orderbook_connect")
    public ResponseEntity orderbookConnect(@RequestParam String pair) {
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

}
