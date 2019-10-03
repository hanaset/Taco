package com.hanaset.tacogenji.web.rest;

import com.hanaset.tacocommon.entity.BalanceEntity;
import com.hanaset.tacocommon.repository.BalanceRepository;
import com.hanaset.tacogenji.api.upbit.UpbitGenjiWebSocketService;
import com.hanaset.tacogenji.service.CryptoSelectService;
import com.hanaset.tacogenji.service.UpbitBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/test")
public class GenjiTestRest {

    private final CryptoSelectService cryptoSelectService;
    private final UpbitGenjiWebSocketService upbitGenjiWebSocketService;
    private final UpbitBalanceService upbitBalanceService;
    private final BalanceRepository balanceRepository;

    public GenjiTestRest(CryptoSelectService cryptoSelectService,
                         UpbitGenjiWebSocketService upbitGenjiWebSocketService,
                         UpbitBalanceService upbitBalanceService,
                         BalanceRepository balanceRepository) {
        this.cryptoSelectService = cryptoSelectService;
        this.upbitGenjiWebSocketService = upbitGenjiWebSocketService;
        this.upbitBalanceService = upbitBalanceService;
        this.balanceRepository = balanceRepository;
    }

    @GetMapping("/connect")
    public String connect() {
        upbitGenjiWebSocketService.orderbookConnect("BTC");
        return "ok";
    }

    @GetMapping("/disconnect")
    public String disconnect(){
        upbitGenjiWebSocketService.orderbookDisconnect();
        return "ok";
    }

    @GetMapping("/balance")
    public ResponseEntity getBalance() {
        balanceRepository.save(BalanceEntity.builder().amount(BigDecimal.valueOf(1234)).build());
        return new ResponseEntity(upbitBalanceService.getUpbitBalance(), HttpStatus.OK);
    }
}
