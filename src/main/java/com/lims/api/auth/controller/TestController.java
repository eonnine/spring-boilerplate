package com.lims.api.auth.controller;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.annotation.LoginCheck;
import com.lims.api.common.annotation.UseAuthToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @LoginCheck
    @GetMapping
    public String test(@UseAuthToken AuthToken token, String value) {
        return "test";
    }
}