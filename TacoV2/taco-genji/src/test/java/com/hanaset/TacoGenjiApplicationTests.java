package com.hanaset;

import com.hanaset.tacocommon.config.JasyptConfig;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import com.hanaset.tacocommon.repository.upbit.UpbitPairRepository;
import com.hanaset.tacogenji.service.CryptoSelectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"management.port=5010", "management.context-path=/management"})
@SpringBootConfiguration
@SpringBootApplication
@ContextConfiguration(classes = {
        UpbitPairRepository.class,
        CryptoSelectService.class,
        JasyptConfig.class,
        TradeKeyProperties.class,
        TradeUrlProperties.class
})
@ActiveProfiles("local")
public class TacoGenjiApplicationTests {

    @Autowired
    UpbitPairRepository upbitPairRepository;

    @Autowired
    CryptoSelectService cryptoSelectService;

    @Test
    public void getCrypto() {

    }
}
