package com.hanaset;

import com.hanaset.tacocommon.config.JasyptConfig;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import com.hanaset.tacocommon.repository.PairRepository;
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
        PairRepository.class,
        JasyptConfig.class,
        TradeKeyProperties.class,
        TradeUrlProperties.class
})
@ActiveProfiles("local")
public class TacoReaperApplicationTests {

    @Autowired
    PairRepository pairRepository;


    @Test
    public void getCrypto() {

    }
}
