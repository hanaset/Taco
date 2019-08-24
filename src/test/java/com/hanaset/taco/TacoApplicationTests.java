package com.hanaset.taco;

import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.api.upbit.model.UpbitOrderResponse;
import com.hanaset.taco.properties.TradeKeyProperties;
import com.hanaset.taco.properties.TradeUrlProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"management.port=5010", "management.context-path=/management"})
@SpringBootConfiguration
@SpringBootApplication
@ContextConfiguration(classes = {
        TradeUrlProperties.class,
        TradeKeyProperties.class

})
public class TacoApplicationTests {

    @Autowired
    UpbitApiRestClient upbitApiRestClient;

    @Test
    public void contextLoads() throws IOException {

        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market("KRW-BTC")
                .side("bid")
                .volume("0.002")
                .price("12000000")
                .ord_type("limit")
                .build();


        //upbitApiRestClient.createOrder(request);

        try {
            String uuid = upbitApiRestClient.createOrder(request).execute().body().getUuid();
            System.out.println(uuid);
            //System.out.println(upbitApiRestClient.deleteOrder(uuid).execute().body().toString());
        }catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

}
