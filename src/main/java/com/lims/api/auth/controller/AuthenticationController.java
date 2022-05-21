package com.lims.api.auth.controller;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.model.AuthenticationRequest;
import com.lims.api.auth.model.AuthenticationResponse;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.common.annotation.UseAuthToken;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthTokenProvider authTokenProvider;
    private final AuthProperties authProperties;

    @PostMapping(value = "token")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws UnAuthenticatedException {
        AuthToken authToken = authTokenProvider.generate(request.getUsername(), request.getPassword());
        return new AuthenticationResponse().toResponseEntity(authProperties, authToken);
    }

    @PostMapping(value = "token/reissue")
    public ResponseEntity<AuthenticationResponse> refreshToken(@UseAuthToken AuthToken token) throws UnAuthenticatedException {
        AuthToken authToken = authTokenProvider.refresh(token.getRefreshToken());
        return new AuthenticationResponse().toResponseEntity(authProperties, authToken);
    }

    @GetMapping(value = "token/verification")
    public ResponseEntity<Boolean> verifyToken(@UseAuthToken AuthToken token) {
        return ResponseEntity.ok().body(authTokenProvider.verify(token.getAccessToken()));
    }

}