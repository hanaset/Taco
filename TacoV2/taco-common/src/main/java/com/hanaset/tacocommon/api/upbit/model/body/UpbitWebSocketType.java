package com.hanaset.tacocommon.api.upbit.model.body;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpbitWebSocketType {
    private String type;
    private List<String> codes;
}