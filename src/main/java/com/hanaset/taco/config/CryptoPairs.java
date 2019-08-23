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
                    "KRW-MTL", "BTC-MTL",
                    "KRW-ETC", "BTC-ETC",
                    "KRW-BCH", "BTC-BCH",
                    "KRW-BSV", "BTC-BSV",
                    "KRW-LTC", "BTC-LTC",
                    "KRW-TSHP", "BTC-TSHP");

}
