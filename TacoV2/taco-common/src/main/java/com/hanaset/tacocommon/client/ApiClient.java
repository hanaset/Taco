package com.hanaset.tacocommon.client;

import com.hanaset.tacocommon.api.upbit.UpbitApiRestClient;
import com.hanaset.tacocommon.properties.TradeKeyProperties;
import com.hanaset.tacocommon.properties.TradeUrlProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EqualsAndHashCode(callSuper = false)
public class ApiClient extends AbstractrestTemplate {

    private final TradeUrlProperties tradeUrlProperties;
    private final TradeKeyProperties tradeKeyProperties;

    public ApiClient(TradeUrlProperties tradeUrlProperties,
                     TradeKeyProperties tradeKeyProperties) {
        this.tradeUrlProperties = tradeUrlProperties;
        this.tradeKeyProperties = tradeKeyProperties;
    }


    @Bean
    public UpbitApiRestClient upbitClient() {
        return new UpbitApiRestClient(tradeKeyProperties, tradeUrlProperties);
    }

}
