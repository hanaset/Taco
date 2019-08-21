package com.hanaset.taco.service;

import com.google.common.collect.Lists;
import com.hanaset.taco.api.bithumb.BithumbRestClient;
import com.hanaset.taco.api.coinone.CoinoneRestClient;
import com.hanaset.taco.api.upbit.UpbitApiRestClient;
import com.hanaset.taco.config.CryptoPairs;
import com.hanaset.taco.item.TransactionItem;
import com.hanaset.taco.utils.Taco2BithumbConvert;
import com.hanaset.taco.utils.Taco2CoinoneConvert;
import com.hanaset.taco.utils.Taco2UpbitConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionHistoryService {

    private final BithumbRestClient bithumbRestClient;
    private final UpbitApiRestClient upbitApiRestClient;
    private final CoinoneRestClient coinoneRestClient;

    public TransactionHistoryService(BithumbRestClient bithumbRestClient,
                                     UpbitApiRestClient upbitApiRestClient,
                                     CoinoneRestClient coinoneRestClient) {
        this.bithumbRestClient = bithumbRestClient;
        this.upbitApiRestClient = upbitApiRestClient;
        this.coinoneRestClient = coinoneRestClient;
    }

    public void getTransactionHistory() {

        List<TransactionItem> transactionItems = Lists.newArrayList();

        CryptoPairs.pairs.stream().forEach(pair -> {
            transactionItems.add(Taco2BithumbConvert.convertTransaction(bithumbRestClient.getRestApi("transaction_history/" + pair), pair));
            transactionItems.add(Taco2UpbitConvert.convertTransaction(upbitApiRestClient.getRestApi("trades/ticks?market=KRW-" + pair), pair));
            transactionItems.add(Taco2CoinoneConvert.convertTransaction(coinoneRestClient.getRestApi("trades?currency=" + pair), pair));
        });

        log.info(transactionItems.toString());
    }
}
