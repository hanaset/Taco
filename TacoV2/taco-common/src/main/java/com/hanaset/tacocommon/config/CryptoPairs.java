package com.hanaset.tacocommon.config;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public class CryptoPairs {

    public static final List<String> UPBIT_PAIRS =
            Lists.newArrayList("KRW-BTC",
                    "KRW-LOOM", "BTC-LOOM",
                    "KRW-XLM", "BTC-XLM"

//                    "KRW-ETH", "BTC-ETH",
//                    "KRW-XRP", "BTC-XRP",
//                    "KRW-EOS", "BTC-EOS",
//                    "KRW-TRX", "BTC-TRX",
//                    "KRW-ADA", "BTC-ADA",
//                    "KRW-XLM", "BTC-XLM",
//                    "KRW-BCH", "BTC-BCH",
//                    "KRW-BTT", "BTC-BTT",
//                    "KRW-BSV", "BTC-BSV"
            );

}
