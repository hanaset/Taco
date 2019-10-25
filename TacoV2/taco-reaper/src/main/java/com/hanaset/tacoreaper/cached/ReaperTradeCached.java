package com.hanaset.tacoreaper.cached;

import com.google.common.collect.Maps;
import com.hanaset.tacocommon.api.okex.model.OkexOrderResponse;
import com.hanaset.tacocommon.api.probit.model.ProbitOrderResponse;
import com.hanaset.tacoreaper.model.ReaperPair;
import com.hanaset.tacoreaper.model.ReaperTradeCondition;

import java.util.Map;

public class ReaperTradeCached {

    public static Map<String, ReaperPair> ASK_PAIR_VALUE = Maps.newHashMap();

    public static Map<String, ReaperPair> BID_PAIR_VALUE = Maps.newHashMap();

    public static ReaperTradeCondition TRADE_CONDITION;

    public static Map<String, ProbitOrderResponse> PROBIT_RESPONSE = Maps.newHashMap();

    public static Map<String, OkexOrderResponse> OKEX_RESPONSE = Maps.newHashMap();

}
