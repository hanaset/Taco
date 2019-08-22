package com.hanaset.taco.service;

import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.utils.Taco2CurrencyConvert;
import com.hanaset.taco.utils.TacoPercentChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpbitAskCheckService {

    public void compareASKWithBID(String pair) {

        try {
            UpbitOrderbookItem btcItem = OrderbookCached.UPBIT.get("BTC-ETH");
            UpbitOrderbookItem krwItem = OrderbookCached.UPBIT.get("KRW-ETH");

            if (TacoPercentChecker.profitCheck(Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(), 0.3)) {

                System.out.println("=======================================");
                System.out.println(String.format("Ask : %f, Bid : %f, profit : %f, percent : %f",
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(),
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                        (Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price() / Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) * 100)));
                System.out.println("=======================================");

            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()), 0.3)) {

                System.out.println("=======================================");
                System.out.println(String.format("Ask : %f, Bid : %f, profit : %f, percent : %f",
                        krwItem.getBid_price(), Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()),
                        krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()),
                        (krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price())) / krwItem.getBid_price() * 100));
                System.out.println("=======================================");

            }

            System.out.println("=======================================");
            System.out.println(String.format("Ask : %f / Bid : %f / profit : %f / percent : %f",
                    Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(),
                    Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                    (Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) * 100));
            System.out.println("=======================================");
            System.out.println("=======================================");
            System.out.println(String.format("Ask : %f / Bid : %f / profit : %f / percent : %f",
                    krwItem.getBid_price(), Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()),
                    krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()),
                    (krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price())) / krwItem.getBid_price() * 100));
            System.out.println("=======================================");

        }catch (NullPointerException e) {
            log.error("Upbit Data Null");
        }
    }
}
