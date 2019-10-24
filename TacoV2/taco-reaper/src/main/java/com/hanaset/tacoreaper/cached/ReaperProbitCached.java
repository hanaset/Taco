package com.hanaset.tacoreaper.cached;

import com.google.common.collect.Maps;
import com.hanaset.tacoreaper.model.ReaperProbitPair;
import com.hanaset.tacoreaper.model.ReaperTradeCondition;

import java.util.Map;

public class ReaperProbitCached {

    public static Map<String, ReaperProbitPair> ASK_PAIR_VALUE = Maps.newHashMap();

    public static Map<String, ReaperProbitPair> BID_PAIR_VALUE = Maps.newHashMap();

    public static ReaperTradeCondition TRADE_CONDITION;

}
