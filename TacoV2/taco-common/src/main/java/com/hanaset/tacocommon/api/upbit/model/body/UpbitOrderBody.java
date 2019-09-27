package com.hanaset.tacocommon.api.upbit.model.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpbitOrderBody {
    private String market;
    private String side;
    private String volume;
    private String price;
    private String ord_type;
}
