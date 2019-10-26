package com.hanaset.tacomccree.web.controller.support;

public abstract class McCreeControllerSupport {

    protected String redirect(String url) {
        return "redirect:".concat(url);
    }

}
