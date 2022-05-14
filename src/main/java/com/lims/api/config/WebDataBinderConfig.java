package com.lims.api.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class WebDataBinderConfig {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.initDirectFieldAccess();
    }

}