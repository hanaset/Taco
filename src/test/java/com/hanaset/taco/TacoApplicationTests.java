package com.hanaset.taco;

import com.google.common.collect.Maps;
import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.api.upbit.model.UpbitMarket;
import com.hanaset.taco.api.upbit.model.UpbitOrderRequest;
import com.hanaset.taco.properties.TradeKeyProperties;
import com.hanaset.taco.properties.TradeUrlProperties;
import com.hanaset.taco.utils.Taco2UpbitConvert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public void 밸런스확인() {
        System.out.println(upbitApiRestClient.getAccount("amount").blockingGet().toString());
    }

    @Test
    public void 마켓확인() {
        List<UpbitMarket> marketList = upbitApiRestClient.getMarket().blockingGet();
        Map<String, Integer> pairs = Maps.newHashMap();

        for(UpbitMarket market : marketList) {
            if(market.getMarket().contains("KRW") || market.getMarket().contains("BTC")) {

                String key = Taco2UpbitConvert.convertPair(market.getMarket());
                if(pairs.containsKey(key)) {
                    pairs.replace(key, pairs.get(key)+1);
                }else {
                    pairs.put(key, 1);
                }
            }
        }

        pairs.forEach((k, v) ->{
            if(v == 2) {
                System.out.println(k);
            }
        });

        //System.out.println(pairs);
    }

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
