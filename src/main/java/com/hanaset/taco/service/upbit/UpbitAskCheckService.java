package com.hanaset.taco.service.upbit;

import com.hanaset.taco.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.taco.cache.OrderbookCached;
import com.hanaset.taco.repository.TransactionHistoryRepository;
import com.hanaset.taco.utils.Taco2CurrencyConvert;
import com.hanaset.taco.utils.TacoPercentChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UpbitAskCheckService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");
    private final TransactionHistoryRepository transactionHistoryRepository;

    public UpbitAskCheckService(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    public void compareASKWithBID(String pair) {

        try {
            UpbitOrderbookItem btcItem = OrderbookCached.UPBIT.get("BTC-" + pair);
            UpbitOrderbookItem krwItem = OrderbookCached.UPBIT.get("KRW-" + pair);

            if (TacoPercentChecker.profitCheck(Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(), 0.3)) {

                log.info("[{}] [Bid : {}/{}] [Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()), btcItem.getBid_size(),
                        krwItem.getAsk_price(), krwItem.getAsk_size(),
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                        (Taco2CurrencyConvert.convertBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / krwItem.getAsk_price() * 100);

            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()), 0.3)) {

                log.info("[{}] [Bid : {}/{}] [Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        krwItem.getBid_price(), krwItem.getBid_size(),
                        Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()), btcItem.getAsk_size(),
                        krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()),
                        (krwItem.getBid_price() - Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price())) / Taco2CurrencyConvert.convertBTC2KRW(btcItem.getAsk_price()) * 100);

            }

        } catch (NullPointerException e) {
            log.error("[{}] Upbit Data Null", pair);
        }
    }


    private void asking(UpbitOrderbookItem askItem, BigDecimal amount) {


        // 매수


    }

    private void biding(UpbitOrderbookItem bidItem, BigDecimal amount) {

        // 매도
    }
}
