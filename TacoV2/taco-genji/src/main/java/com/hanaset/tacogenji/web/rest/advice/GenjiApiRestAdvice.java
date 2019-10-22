package com.hanaset.tacogenji.web.rest.advice;

import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacogenji.web.rest.support.GenjiApiRestSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenjiApiRestAdvice extends GenjiApiRestSupport {

    @ExceptionHandler(TacoResponseException.class)
    public ResponseEntity handleSkyResponseException(TacoResponseException ex){
        return skyResponseException(ex.getCode(), ex.getMessage());
    }

}
