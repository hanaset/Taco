package com.hanaset.tacocommon.api.huobi.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HuobiDepthTick {

    private Long version;

    private Long ts;

    private List<List<Double>> bids;

    private List<List<Double>> asks;

}
