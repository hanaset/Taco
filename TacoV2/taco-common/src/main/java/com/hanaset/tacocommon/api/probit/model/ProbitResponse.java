package com.hanaset.tacocommon.api.probit.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProbitResponse<T> {

    private T data;
}
