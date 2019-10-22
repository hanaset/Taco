package com.hanaset.tacocommon.exception;

public class TacoResponseException extends RuntimeException {

    private String code;

    public TacoResponseException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() { return this.code; }
}
