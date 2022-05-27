package com.lims.api.auth.controller;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.model.AuthenticationRequest;
import com.lims.api.auth.model.TokenResponse;
import com.lims.api.auth.service.TokenAuthenticationService;
import com.lims.api.auth.service.TokenHttpHelper;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.annotation.UseAuthToken;
import com.lims.api.common.domain.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final TokenAuthenticationService authenticationService;
    private final TokenService tokenService;
    private final TokenHttpHelper tokenHttpHelper;

    @PostMapping(value = "token")
    public ResponseEntity<TokenResponse> login(@Validated @RequestBody AuthenticationRequest request) throws UnAuthenticatedException {
        AuthToken authToken = authenticationService.authenticate(request.getUsername(), request.getPassword());
        return tokenHttpHelper.toResponseEntity(HttpStatus.CREATED, authToken);
    }

    @PostMapping(value = "token/reissue")
    public ResponseEntity<TokenResponse> refreshToken(@UseAuthToken AuthToken token) throws UnAuthenticatedException {
        AuthToken authToken = authenticationService.authenticate(token.getRefreshToken());
        return tokenHttpHelper.toResponseEntity(HttpStatus.CREATED, authToken);
    }

    @GetMapping(value = "token/verification")
    public ResponseEntity<ValidationResult> verifyToken(@UseAuthToken AuthToken token) {
        return ResponseEntity.ok().body(tokenService.verifyResult(token.getAccessToken()));
    }

}