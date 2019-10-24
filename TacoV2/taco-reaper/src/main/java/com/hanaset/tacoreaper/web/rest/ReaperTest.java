package com.hanaset.tacoreaper.web.rest;

import com.hanaset.tacocommon.api.probit.ProbitApiRestClient;
import com.hanaset.tacocommon.api.probit.model.ProbitOrderCancelRequest;
import com.hanaset.tacocommon.api.probit.model.ProbitOrderRequest;
import com.hanaset.tacocommon.api.probit.model.ProbitOrderResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/test")
public class ReaperTest {

    private final ProbitApiRestClient probitApiRestClient;

    public ReaperTest(ProbitApiRestClient probitApiRestClient) {
        this.probitApiRestClient = probitApiRestClient;
    }

    @GetMapping("/order")
    public void orderTest() {
        ProbitOrderRequest request = ProbitOrderRequest.builder()
                .makretId("XRP-KRW") // PROBIT의 마켓 ID는 XRP-KRW 형식이다.
                .side("buy")
                .type("limit")
                .timeInForce("gtc")
                .limitPrice("316")
                .quantity("10")
                .build();

        ProbitOrderResponse response = probitApiRestClient.order(request);
        System.out.println(response);

        try {
            Thread.sleep(5000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        ProbitOrderCancelRequest orderCancelRequest = ProbitOrderCancelRequest.builder()
                .orderId(response.getId())
                .marketId(response.getMarketId())
                .build();

        response = probitApiRestClient.cancelOrder(orderCancelRequest);
        System.out.println(response);

    }
}
