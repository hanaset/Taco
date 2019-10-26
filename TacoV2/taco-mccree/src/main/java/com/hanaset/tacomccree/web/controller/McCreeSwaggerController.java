package com.hanaset.tacomccree.web.controller;

import com.hanaset.tacomccree.web.controller.support.McCreeControllerSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class McCreeSwaggerController extends McCreeControllerSupport {

    @GetMapping("/swagger")
    public String redirect() {
        return redirect("/swagger-ui.html");
    }

}
