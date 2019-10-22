package com.hanaset.tacocommon.api.huobi;

import com.hanaset.tacocommon.common.BaseParamsDigest;

import javax.crypto.Mac;
import java.util.Base64;

public class HuobiDigest extends BaseParamsDigest {

    private HuobiDigest(String secretKey) {

        super(secretKey, HMAC_SHA_256);
    }

    public static HuobiDigest createInstance(String secretKey) {

        return secretKey == null ? null : new HuobiDigest(secretKey);
    }

    @Override
    public String getDigest(String input) {
        Mac mac = getMac();
        return Base64.getEncoder().encodeToString(mac.doFinal(input.getBytes())).trim();
    }
}