package com.hanaset.tacocommon.cache;

import com.hanaset.tacocommon.api.upbit.model.UpbitTicket;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpbitTransactionCached {

    public static UpbitTicket TICKET = null;

    public static String PAIR = null;

    public static Boolean LOCK = false;

    public static BigDecimal btcAmount;

    public static BigDecimal pairAmount;

    public static void reset() {
        TICKET = null;
        LOCK = false;
    }
}
