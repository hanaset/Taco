package com.hanaset.tacogenji.web.controller;

import com.hanaset.tacogenji.web.controller.support.TacoControllerSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TacoSwaggerController extends TacoControllerSupport {

    @GetMapping("/swagger")
    public String redirect() {return redirect("/swagger-ui.html");}

}
