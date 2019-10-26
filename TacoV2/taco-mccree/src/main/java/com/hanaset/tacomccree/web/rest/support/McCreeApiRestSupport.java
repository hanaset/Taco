package com.hanaset.tacomccree.web.rest.support;

import com.google.common.collect.ImmutableMap;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class McCreeApiRestSupport {

    protected static <T> ResponseEntity<?> success(T data) {
        return ResponseEntity.ok(
                ImmutableMap.of(
                        "code", "0",
                        "data", data
                )
        );
    }

    protected static <T> ResponseEntity<?> response(T data) {
        return ResponseEntity.ok(
                ImmutableMap.of(
                        "code", "0",
                        "data", data != null ? data : new JSONObject()
                )
        );
    }

    protected static <T> ResponseEntity<?> reaperResponseException(String code, String msg) {
        return new ResponseEntity<>(
                ImmutableMap.of(
                        "code", code,
                        "msg", msg,
                        "data", "{}"
                ), HttpStatus.BAD_REQUEST
        );
    }
}
