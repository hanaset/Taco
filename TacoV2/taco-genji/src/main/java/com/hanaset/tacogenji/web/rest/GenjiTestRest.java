package com.hanaset.tacogenji.web.rest;

import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
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
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/test")
public class GenjiTestRest {

    private final CryptoSelectService cryptoSelectService;
    private final UpbitGenjiWebSocketService upbitGenjiWebSocketService;
    private final UpbitBalanceService upbitBalanceService;
    private final BalanceRepository balanceRepository;
    private final UpbitApiRestClient upbitApiRestClient;

    public GenjiTestRest(CryptoSelectService cryptoSelectService,
                         UpbitGenjiWebSocketService upbitGenjiWebSocketService,
                         UpbitBalanceService upbitBalanceService,
                         BalanceRepository balanceRepository,
                         UpbitApiRestClient upbitApiRestClient) {
        this.cryptoSelectService = cryptoSelectService;
        this.upbitGenjiWebSocketService = upbitGenjiWebSocketService;
        this.upbitBalanceService = upbitBalanceService;
        this.balanceRepository = balanceRepository;
        this.upbitApiRestClient = upbitApiRestClient;
    }

    @GetMapping("/connect")
    public String connect() {
        upbitGenjiWebSocketService.orderbookConnect("BTC");
        return "ok";
    }

    @GetMapping("/disconnect")
    public String disconnect() {
        upbitGenjiWebSocketService.orderbookDisconnect();
        return "ok";
    }

    @GetMapping("/balance")
    public ResponseEntity getBalance() {
        balanceRepository.save(BalanceEntity.builder().amount(BigDecimal.valueOf(1234)).build());
        return new ResponseEntity(upbitBalanceService.getUpbitBalance(), HttpStatus.OK);
    }

    @GetMapping("/test")
    public void test() {

        try {
            Response<List<Object>> response = upbitApiRestClient.getGameLog().execute();

            if(response.isSuccessful()) {
                System.out.println(response.body());
            } else {
                System.out.println(response.errorBody().byteString());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
