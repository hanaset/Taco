package com.hanaset.tacomccree.web.rest.advice;

import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacomccree.web.rest.support.McCreeApiRestSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class McCreeApiRestAdvice extends McCreeApiRestSupport {

    @ExceptionHandler(TacoResponseException.class)
    public ResponseEntity handleMcCreeResponseException(TacoResponseException ex) {
        return reaperResponseException(ex.getCode(), ex.getMessage());
    }

}