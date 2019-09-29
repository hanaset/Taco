package com.hanaset.tacomercy.service;

import com.hanaset.tacocommon.api.upbit.model.UpbitOrderbookItem;
import com.hanaset.tacocommon.cache.OrderbookCached;
import com.hanaset.tacocommon.cache.UpbitTransactionCached;
import com.hanaset.tacocommon.utils.DateTimeUtils;
import com.hanaset.tacocommon.utils.Taco2CurrencyConvert;
import com.hanaset.tacocommon.utils.TacoPercentChecker;
import com.hanaset.tacocommon.entity.TransactionLogEntity;
import com.hanaset.tacocommon.repository.TransactionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@SuppressWarnings("Duplicates")
public class UpbitPairSearchService {

    private Logger log = LoggerFactory.getLogger("upbit_askbid");

    private final TransactionLogRepository transactionLogRepository;
    private final Double profit = 0.4;



    public UpbitPairSearchService(TransactionLogRepository transactionLogRepository) {
        this.transactionLogRepository = transactionLogRepository;
    }

    @Async
    public void checkProfit(String pair) {

        try {
            UpbitOrderbookItem btcItem = OrderbookCached.UPBIT.getOrDefault("BTC-" + pair, null);
            UpbitOrderbookItem krwItem = OrderbookCached.UPBIT.getOrDefault("KRW-" + pair, null);

            if (btcItem == null || krwItem == null)
                return;

            if (TacoPercentChecker.profitCheck(Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), krwItem.getAsk_price(), profit)) {

                Double base_amount = btcItem.getBid_size() > krwItem.getAsk_size() ? krwItem.getAsk_size() : btcItem.getBid_size();
                Double amount = base_amount / 10.f;

                if (amount * btcItem.getBid_price() <= 0.0005 || amount * krwItem.getAsk_price() <= 10000) {
                    return;
                }

                log.info("==================================================================");

                log.info("[{}] [BTC Bid : {}({})/{}] [KRW Ask : {}/{}] [profit : {}] [percent : {}]",
                        pair,
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()), BigDecimal.valueOf(btcItem.getBid_price()).toPlainString(), btcItem.getBid_size(),
                        krwItem.getAsk_price(), krwItem.getAsk_size(),
                        Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price(),
                        (Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / krwItem.getAsk_price() * 100);

                TransactionLogEntity entity = TransactionLogEntity.builder()
                        .crypto(pair)
                        .profitAmount(BigDecimal.valueOf(Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()).multiply(BigDecimal.valueOf(base_amount)))
                        .profitPercent(BigDecimal.valueOf((Taco2CurrencyConvert.convertBidBTC2KRW(btcItem.getBid_price()) - krwItem.getAsk_price()) / krwItem.getAsk_price() * 100))
                        .snapshot(DateTimeUtils.getCurrentDay("Asia/Seoul"))
                        .build();

                transactionLogRepository.save(entity);

            } else if (TacoPercentChecker.profitCheck(krwItem.getBid_price(), Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), profit)) {

                Double base_amount = krwItem.getBid_size() > btcItem.getAsk_size() ? btcItem.getAsk_size() : krwItem.getBid_size();
                Double amount = base_amount / 10.f;

                if (amount * btcItem.getAsk_price() <= 0.0005 || amount * krwItem.getBid_price() <= 10000) {
                    return;
                }

                log.info("==================================================================");

                log.info("[{}] [KRW Bid : {}/{}] [BTC Ask : {}({})/{}] [profit : {}] [percent : {}]",
                        pair,
                        krwItem.getBid_price(), krwItem.getBid_size(),
                        Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()), BigDecimal.valueOf(btcItem.getAsk_price()).toPlainString(), btcItem.getAsk_size(),
                        krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()),
                        (krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price())) / Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()) * 100);

                TransactionLogEntity entity = TransactionLogEntity.builder()
                        .crypto(pair)
                        .profitAmount(BigDecimal.valueOf(krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price())).multiply(BigDecimal.valueOf(base_amount)))
                        .profitPercent(BigDecimal.valueOf((krwItem.getBid_price() - Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price())) / Taco2CurrencyConvert.convertAskBTC2KRW(btcItem.getAsk_price()) * 100))
                        .snapshot(DateTimeUtils.getCurrentDay("Asia/Seoul"))
                        .build();

                transactionLogRepository.save(entity);
            }

        } catch (Exception e) {
            log.error("[{}] Upbit Data error -> {}", pair, e.getMessage());
            UpbitTransactionCached.LOCK = false;
        }
    }


}
