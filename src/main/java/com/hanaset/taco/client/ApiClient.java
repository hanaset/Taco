package com.hanaset.taco.client;


import com.hanaset.taco.api.bithumb.BithumbRestClient;
import com.hanaset.taco.api.coinone.CoinoneRestClient;
import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.properties.TradeKeyProperties;
import com.hanaset.taco.properties.TradeUrlProperties;
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
    public BithumbRestClient bithumbClient() {
        return new BithumbRestClient(tradeUrlProperties.getBithumbPublicUrl(),
                defaultRestTemplate());
    }

    @Bean
    public UpbitApiRestClient upbitClient() {
        return new UpbitApiRestClient(tradeUrlProperties.getUpbitPublicUrl(),
                defaultRestTemplate(),
                tradeKeyProperties);
    }

    @Bean
    public CoinoneRestClient coinoneClient(){
        return new CoinoneRestClient(tradeUrlProperties.getCoinonePublicUrl(),
                defaultRestTemplate());
    }


}
