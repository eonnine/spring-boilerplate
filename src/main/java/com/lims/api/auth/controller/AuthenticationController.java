package com.lims.api.auth.controller;

import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.AuthTokenProvider;
import com.lims.api.auth.model.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthTokenProvider authTokenProvider;

    @PostMapping(value = "login")
    public ResponseEntity<AuthToken> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok().body(authTokenProvider.generate(request.getUsername(), request.getPassword()));
    }

}