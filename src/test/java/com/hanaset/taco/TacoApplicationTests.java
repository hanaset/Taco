package com.hanaset.taco;

import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
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
    public void contextLoads() {

        UpbitOrderRequest request = UpbitOrderRequest.builder()
                .market("KRW-ETH")
                .side("ask")
                .volume("0.003")
                .price("232250")
                .ord_type("limit")
                .build();

        System.out.println(upbitApiRestClient.createOrder(request));
    }

}
