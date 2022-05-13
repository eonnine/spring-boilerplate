package com.lims.api.auth.controller;

import com.lims.api.auth.domain.AuthJWT;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.AuthTokenProvider;
import com.lims.api.auth.model.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthTokenProvider authTokenProvider;

    @PostMapping(value = "login")
    public ResponseEntity<AuthToken> login(@RequestBody AuthenticationRequest request) {
        AuthToken authToken = authTokenProvider.generate(request.getUsername(), request.getPassword());

        // TODO refactoring
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "access-token=" + authToken.getAccessToken() + "; HttpOnly; SameSite=strict;");
        headers.add("Set-Cookie", "refresh-token=" + authToken.getRefreshToken() + "; HttpOnly; SameSite=strict;");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(authToken);
    }

    @PostMapping(value = "verification")
    public ResponseEntity<Boolean> verifyToken(@RequestBody AuthJWT token) {
        return ResponseEntity.ok().body(authTokenProvider.verify(token.getAccessToken()));
    }

    @PostMapping(value = "token")
    public ResponseEntity<AuthToken> refreshToken(@RequestBody AuthJWT token) {
        return ResponseEntity.ok().body(authTokenProvider.refresh(token));
    }

}