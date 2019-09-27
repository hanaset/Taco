package com.hanaset.tacocommon.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:properties/trade/trade-url-${spring.profiles.active}.properties")
public class TradeUrlProperties {

    @Value("${bithumb.public}")
    private String bithumbPublicUrl;

    @Value("${upbit.public}")
    private String upbitPublicUrl;

    @Value("${upbit.websocket}")
    private String upbitWebSockUrl;

    @Value("${coinone.public}")
    private String coinonePublicUrl;

}
