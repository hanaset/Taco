package com.hanaset.tacocommon.api.upbit.model;

import lombok.Data;

@Data
public class UpbitOrderResponse {

    private String uuid;
    private String side;
    private String ord_type;
    private Double price;
    private Double avg_price;
    private String state;
    private String market;
    private String created_at;
    private Double volume;
    private Double remaining_volume;
    private Double reserved_fee;
    private Double remaining_fee;
    private Double paid_fee;
    private Double locked;
    private Double executed_volume;
    private Double trades_count;
    //private List<UpbitTradeItem> trades;
}
