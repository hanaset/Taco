package com.hanaset.tacoreaper.web.rest;

import com.hanaset.tacocommon.api.okex.OkexApiRestClient;
import com.hanaset.tacocommon.api.okex.model.OkexOrderDetail;
import com.hanaset.tacocommon.api.okex.model.OkexOrderRequest;
import com.hanaset.tacocommon.api.okex.model.OkexOrderResponse;
import com.hanaset.tacocommon.api.probit.ProbitApiRestClient;
import com.hanaset.tacoreaper.service.okex.ReaperOkexService;
import com.hanaset.tacoreaper.web.rest.support.ReaperApiRestSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/test")
public class ReaperTest extends ReaperApiRestSupport {

    private final ProbitApiRestClient probitApiRestClient;
    private final OkexApiRestClient okexApiRestClient;

    private final ReaperOkexService reaperOkexService;

    public ReaperTest(ProbitApiRestClient probitApiRestClient,
                      OkexApiRestClient okexApiRestClient,
                      ReaperOkexService reaperOkexService) {
        this.probitApiRestClient = probitApiRestClient;
        this.okexApiRestClient = okexApiRestClient;
        this.reaperOkexService = reaperOkexService;
    }

    @GetMapping("/order")
    public ResponseEntity orderTest() {

        OkexOrderRequest request = OkexOrderRequest.builder()
                .instrumentId("XRP-KRW")
                .side("buy")
                .price("347")
                .size("10")
                .build();

        OkexOrderResponse response = okexApiRestClient.order(request);

        System.out.println(response);

        OkexOrderDetail detail = okexApiRestClient.orderDetail(response.getOrderId(), request.getInstrumentId());

        System.out.println(detail);

        return success(okexApiRestClient.cancelOrder(response.getOrderId(), request));


    }

    @GetMapping("/account")
    public ResponseEntity accountTest() {
        return success(reaperOkexService.getAccount());
    }
}
