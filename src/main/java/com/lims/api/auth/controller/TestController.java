package com.lims.api.auth.controller;

import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.UseAuthToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public String test(@UseAuthToken AuthToken token, String value) {
        return "test";
    }
}