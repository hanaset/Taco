package com.hanaset.taco.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.MessageDigest;

@Slf4j
public class HashConvert {

    public static String getSHA512(String input) {

        String toReturn = null;

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
        }catch (Exception e) {
            log.info("SHA512 convert Error -> {}", input);
        }

        return toReturn;
    }
}
