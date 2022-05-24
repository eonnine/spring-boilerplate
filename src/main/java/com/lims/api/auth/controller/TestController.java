package com.lims.api.auth.controller;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.model.AuthenticationRequest;
import com.lims.api.common.annotation.UseAuthToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("test")
public class TestController {

//    @LoginCheck
    @PostMapping
    public String test(@UseAuthToken AuthToken token, @RequestBody AuthenticationRequest request) {
        System.out.println(token.toString());
        return request.toString();
    }

    @PutMapping
    public String test2(@UseAuthToken AuthToken token, @RequestBody AuthenticationRequest request) {
        System.out.println(token.toString());
        return request.toString();
    }

    @PatchMapping
    public String test3(@UseAuthToken AuthToken token, @RequestBody Map request) {
        System.out.println(token.toString());
        return request.toString();
    }

    @DeleteMapping
    public String test4(@UseAuthToken AuthToken token, @RequestParam("value") String value) {
        return value;
    }


    @GetMapping("value")
    public String test5(@UseAuthToken AuthToken token, @RequestParam("id") String value) {
        return value;
    }

    @GetMapping("value/{id}")
    public String test6(@UseAuthToken AuthToken token, @PathVariable("id") String id) {
        return id;
    }


}