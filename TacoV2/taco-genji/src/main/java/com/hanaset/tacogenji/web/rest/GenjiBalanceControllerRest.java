package com.hanaset.tacogenji.web.rest;

import com.hanaset.tacocommon.utils.DateTimeUtils;
import com.hanaset.tacogenji.api.upbit.UpbitGenjiWebSocketService;
import com.hanaset.tacogenji.service.CryptoSelectService;
import com.hanaset.tacogenji.service.UpbitBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/control")
public class GenjiBalanceControllerRest {

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

    @GetMapping("/init")
    public ResponseEntity init() {
        upbitGenjiWebSocketService.orderbookDisconnect();
        upbitBalanceService.initBalance();
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @GetMapping("/start")
    public ResponseEntity start() {
        String pair = cryptoSelectService.getPair(DateTimeUtils.getCurrentBeforeNDay("yyyy-MM-dd", "Asia/Seoul", 2), DateTimeUtils.getCurrentDay("Asia/Seoul"));
        upbitBalanceService.startBalance(pair);
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @GetMapping("/current_start")
    public ResponseEntity currentStart() {
        String pair = cryptoSelectService.getCurrentPair(DateTimeUtils.getCurrentDay("Asia/Seoul"));
        upbitBalanceService.startBalance(pair);
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

    @GetMapping("/pair_start")
    public ResponseEntity pairStart(@RequestParam String pair) {
        upbitBalanceService.startBalance(pair);
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }


    @GetMapping("/orderbook_connect")
    public ResponseEntity orderbookConnect(@RequestParam String pair) {
        upbitGenjiWebSocketService.orderbookConnect(pair);
        return new ResponseEntity("OK", HttpStatus.OK);
    }

}
