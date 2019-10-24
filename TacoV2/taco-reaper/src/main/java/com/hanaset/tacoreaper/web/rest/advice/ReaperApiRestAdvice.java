package com.hanaset.tacoreaper.web.rest.advice;

import com.hanaset.tacocommon.exception.TacoResponseException;
import com.hanaset.tacoreaper.web.rest.support.ReaperApiRestSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReaperApiRestAdvice extends ReaperApiRestSupport {

    @ExceptionHandler(TacoResponseException.class)
    public ResponseEntity handleSkyResponseException(TacoResponseException ex) {
        return reaperResponseException(ex.getCode(), ex.getMessage());
    }

}