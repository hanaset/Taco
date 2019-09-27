package com.hanaset.tacocommon.cache;

import com.hanaset.tacocommon.api.upbit.model.UpbitTicket;
import lombok.Data;

@Data
public class UpbitTransactionCached {

    public static UpbitTicket TICKET = null;

    public static int COUNT = 0;

    public static Boolean LOCK = false;

    public static void reset() {
        TICKET = null;
        COUNT = 0;
        LOCK = false;
    }


}
