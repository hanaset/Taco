package com.hanaset.tacocommon.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:properties/key/key-${spring.profiles.active}.properties")
public class TradeKeyProperties {

    @Value("${upbit.accessKey}")
    private String upbitAccessKey;

    @Value("${upbit.secretKey}")
    private String upbitSecretKey;

    @Value("${okex.accessKey}")
    private String okexAccessKey;

    @Value("${okex.secretKey}")
    private String okexSecretKey;

    @Value("${huobi.accessKey}")
    private String huobiAccessKey;

    @Value("${huobi.secretKey")
    private String huobiSecretKey;
}