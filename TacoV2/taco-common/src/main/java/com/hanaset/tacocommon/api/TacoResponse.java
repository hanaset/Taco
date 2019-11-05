package com.hanaset.tacocommon.api;

import com.hanaset.tacocommon.exception.TacoResponseException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

import java.io.IOException;

@Data
@Builder
@Slf4j
public class TacoResponse {

    public static void response(Response response, String code, String msg) {

        try {
            if (!response.isSuccessful()) {
                //System.out.println(response.errorBody().byteString().toString());
                log.error("{} : {}", msg, response.errorBody().byteString().toString());
                //throw new TacoResponseException(code, msg + " : " + response.errorBody().byteString().toString());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
