package com.hanaset.tacogenji.web.controller.support;

public abstract class TacoControllerSupport {

    protected String redirect(String url) {
        return "redirect:".concat(url);
    }
}
