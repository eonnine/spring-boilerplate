package com.lims.api.auth.controller;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.annotation.UseAuthToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestController {

//    @LoginCheck
    @GetMapping
    public String test(@UseAuthToken AuthToken token, String id) {
        return id;
    }

    @PostMapping
    public String test2(@UseAuthToken AuthToken token, @RequestBody String id) {
        return id;
    }

    @GetMapping("value/{id}")
    public String test20(@UseAuthToken AuthToken token, @PathVariable("id") String id) {
        return id;
    }

}