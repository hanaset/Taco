package com.hanaset.tacoreaper.web.controller.support;

public abstract class ReaperControllerSupport {

    protected String redirect(String url) {
        return "redirect:".concat(url);
    }

}
