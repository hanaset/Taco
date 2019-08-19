package com.hanaset.taco.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hanaset.taco.client.trade.BithumbClient;
import com.hanaset.taco.client.trade.CoinoneClient;
import com.hanaset.taco.client.trade.UpbitClient;
import com.hanaset.taco.config.CryptoPairs;
import com.hanaset.taco.item.TransactionItem;
import com.hanaset.taco.utils.Taco2BithumbConvert;
import com.hanaset.taco.utils.Taco2CoinoneConvert;
import com.hanaset.taco.utils.Taco2UpbitConvert;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class TransactionHistoryService {

    CryptoPairs cryptoPairs;

    private final BithumbClient bithumbClient;
    private final UpbitClient upbitClient;
    private final CoinoneClient coinoneClient;

    public TransactionHistoryService(BithumbClient bithumbClient,
                                     UpbitClient upbitClient,
                                     CoinoneClient coinoneClient) {
        this.bithumbClient = bithumbClient;
        this.upbitClient = upbitClient;
        this.coinoneClient = coinoneClient;
    }

    public void getTransactionHistory() {

        List<TransactionItem> transactionItems = Lists.newArrayList();

        cryptoPairs.pairs.stream().forEach(pair -> {
            transactionItems.add(Taco2BithumbConvert.convertTransaction(bithumbClient.getRestApi("transaction_history/" + pair), pair));
            transactionItems.add(Taco2UpbitConvert.convertTransaction(upbitClient.getRestApi("trades/ticks?market=KRW-" + pair), pair));
            transactionItems.add(Taco2CoinoneConvert.convertTransaction(coinoneClient.getRestApi("trades?currency=" + pair), pair));
        });

        log.info(transactionItems.toString());
    }
}
