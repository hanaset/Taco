package com.hanaset.tacocommon.api.okex.model.body;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OkexWebSocketOp {

    private String op;

    private List<String> args;

}
