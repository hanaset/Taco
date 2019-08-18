package com.hanaset.taco.web.rest;

import com.hanaset.taco.service.TickerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticker")
public class TickerApiRest {

    private final TickerService tickerService;

    public TickerApiRest(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    @GetMapping()
    public void getTickerList() {
        tickerService.getTicekrList();
    }
}
