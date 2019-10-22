package com.hanaset.tacocommon.api.huobi.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HuobiAccount {

    private Long id;

    private String type;

    private String state;

    private List<HuobiBalance> list;
}
