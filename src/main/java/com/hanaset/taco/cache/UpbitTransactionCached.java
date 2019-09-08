package com.hanaset.taco.cache;

import com.hanaset.taco.api.upbit.model.UpbitTicket;
import lombok.Data;

@Data
public class UpbitTransactionCached {

    public static UpbitTicket TICKET = null;

    public static int COUNT = 0;

    public static Boolean LOCK = false;


}
