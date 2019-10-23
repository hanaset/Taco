package com.hanaset.tacocommon.api.probit;

import com.hanaset.tacocommon.api.probit.model.ProbitTokenBody;
import com.hanaset.tacocommon.api.probit.model.ProbitTokenResponse;
import org.springframework.web.bind.annotation.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ProbitApiRestService {

    @POST("/token")
    Call<ProbitTokenResponse> getToken(@Header("Authorization") String auth, @Body ProbitTokenBody body);
}
