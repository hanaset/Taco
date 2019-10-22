package com.hanaset.tacoreaper.service;

import com.hanaset.tacocommon.api.huobi.HuobiApiRestClient;
import com.hanaset.tacocommon.api.huobi.model.HuobiDepth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
@Service
public class ReaperHuobiOrderbookService {

    private final HuobiApiRestClient huobiApiRestClient;

    public ReaperHuobiOrderbookService(HuobiApiRestClient huobiApiRestClient) {
        this.huobiApiRestClient = huobiApiRestClient;
    }

    public void getOrderbook() {

        try {
            Response<HuobiDepth> response = huobiApiRestClient.getDepth("bchbtc", "step0").execute();

            if (response.isSuccessful()) {
                System.out.println(response.body().toString());
            } else {
                System.out.println(response.errorBody().byteString().toString());
            }
        } catch (IOException e) {
            log.error("ORDERBOOK error : {}", e.getMessage());
        }
    }
}
