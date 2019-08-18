package com.hanaset.taco.config;

import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;

@Getter
public class CryptoPairs {

    public static final Set<String> pairs = Sets.newHashSet( "BTC", "ETH", "XRP", "LTC", "BCH", "EOS" );

}
