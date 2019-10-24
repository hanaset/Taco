package com.hanaset.tacoreaper.web.controller;

import com.hanaset.tacoreaper.web.controller.support.ReaperControllerSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReaperSwaggerController extends ReaperControllerSupport {

    @GetMapping("/swagger")
    public String redirect() {
        return redirect("/swagger-ui.html");
    }

}
