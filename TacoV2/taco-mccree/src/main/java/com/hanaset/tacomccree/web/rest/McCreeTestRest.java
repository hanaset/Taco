package com.hanaset.tacomccree.web.rest;

import com.hanaset.tacomccree.service.upbit.McCreeUpbitTradeService;
import com.hanaset.tacomccree.web.rest.support.McCreeApiRestSupport;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "TEST REST API", value = "TEST REST API")
@RestController
@RequestMapping("/test")
public class McCreeTestRest extends McCreeApiRestSupport {

    private McCreeUpbitTradeService mcCreeUpbitTradeService;

    public McCreeTestRest(McCreeUpbitTradeService mcCreeUpbitTradeService) {
        this.mcCreeUpbitTradeService = mcCreeUpbitTradeService;
    }
}
