package com.hanaset.taco.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class CryptoPairs {

    public static final Set<String> pairs = Sets.newHashSet( "BTC", "ETH", "XRP", "LTC", "BCH", "EOS" );

    public static final List<String> UPBIT_PAIRS =
            Lists.newArrayList( "KRW-ETH", "BTC-ETH",
                    "KRW-XRP", "BTC-XRP",
                    "KRW-ETC", "BTC-ETC",
                    "KRW-BCH", "BTC-BCH",
                    "KRW-EOS", "BTC-EOS",
                    "KRW-MTL", "BTC-MTL",
                    "KRW-TTC", "BTC-TTC",

                    "KRW-TSHP", "BTC-TSHP",
                    "KRW-BSV", "BTC-BSV"
            );

}
