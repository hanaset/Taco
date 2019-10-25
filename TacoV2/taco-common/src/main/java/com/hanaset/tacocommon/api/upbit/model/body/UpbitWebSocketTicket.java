package com.hanaset.tacocommon.api.upbit.model.body;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpbitWebSocketTicket {
    private String ticket;
}
