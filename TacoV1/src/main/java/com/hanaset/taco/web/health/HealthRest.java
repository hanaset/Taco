package com.hanaset.taco.web.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class HealthRest {

    @GetMapping
    public String getDefault() {
        return "OK";
    }

    @GetMapping("/health")
    public String getHealth() {
        return "OK";
    }
}
