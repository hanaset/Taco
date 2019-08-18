package com.hanaset.taco.client;


import com.hanaset.taco.client.trade.BithumbClient;
import com.hanaset.taco.client.trade.UpbitClient;
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

    public ApiClient(TradeUrlProperties tradeUrlProperties) {
        this.tradeUrlProperties = tradeUrlProperties;
    }


    @Bean
    public BithumbClient bithumbClient() {
        return new BithumbClient(tradeUrlProperties.getBithumbPublicUrl(),
                defaultRestTemplate());
    }

    @Bean
    public UpbitClient upbitClient() {
        return new UpbitClient(tradeUrlProperties.getUpbitPublicUrl(),
                defaultRestTemplate());
    }


}
