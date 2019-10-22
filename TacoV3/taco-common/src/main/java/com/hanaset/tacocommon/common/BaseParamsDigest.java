package com.hanaset.tacocommon.common;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public abstract class BaseParamsDigest implements TacoDigest{

    public static final String HMAC_SHA_512 = "HmacSHA512";
    public static final String HMAC_SHA_384 = "HmacSHA384";
    public static final String HMAC_SHA_256 = "HmacSHA256";
    public static final String HMAC_SHA_1 = "HmacSHA1";
    public static final String HMAC_MD5 = "HmacMD5";

    private final ThreadLocal<Mac> threadLocalMac;

    /**
     * Constructor
     *
     * @param secretKeyBase64 Base64 secret key
     * @throws IllegalArgumentException if key is invalid (cannot be base-64-decoded or the decoded
     *                                  key is invalid).
     */
    protected BaseParamsDigest(String secretKeyBase64, final String hmacString)
            throws IllegalArgumentException {

        final SecretKey secretKey = new SecretKeySpec(secretKeyBase64.getBytes(StandardCharsets.UTF_8), hmacString);
        threadLocalMac =
                new ThreadLocal<Mac>() {

                    @Override
                    protected Mac initialValue() {

                        try {
                            Mac mac = Mac.getInstance(hmacString);
                            mac.init(secretKey);
                            return mac;
                        } catch (InvalidKeyException e) {
                            throw new IllegalArgumentException("Invalid key for hmac initialization.", e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(
                                    "Illegal algorithm for post body digest. Check the implementation.");
                        }
                    }
                };
    }

    /**
     * Constructor
     *
     * @param secretKeyBase64 Base64 secret key
     * @throws IllegalArgumentException if key is invalid (cannot be base-64-decoded or the decoded
     *                                  key is invalid).
     */
    protected BaseParamsDigest(byte[] secretKeyBase64, final String hmacString)
            throws IllegalArgumentException {

        final SecretKey secretKey = new SecretKeySpec(secretKeyBase64, hmacString);
        threadLocalMac =
                new ThreadLocal<Mac>() {

                    @Override
                    protected Mac initialValue() {

                        try {
                            Mac mac = Mac.getInstance(hmacString);
                            mac.init(secretKey);
                            return mac;
                        } catch (InvalidKeyException e) {
                            throw new IllegalArgumentException("Invalid key for hmac initialization.", e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(
                                    "Illegal algorithm for post body digest. Check the implementation.");
                        }
                    }
                };
    }

    protected static byte[] decodeBase64(String secretKey) {
        return Base64.getDecoder().decode(secretKey);
    }

    protected Mac getMac() {

        return threadLocalMac.get();
    }
}
