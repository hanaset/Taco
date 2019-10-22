package com.hanaset.tacocommon.api;

import com.hanaset.tacocommon.exception.TacoResponseException;
import lombok.Builder;
import lombok.Data;
import retrofit2.Response;

@Data
@Builder
public class TacoResponse {

    public static void response(Response response, String code, String msg) {

        if(!response.isSuccessful()) {
            throw new TacoResponseException(code, msg);
        }
    }

}
