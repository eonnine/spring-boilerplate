package com.lims.api.auth.controller;

import com.lims.api.auth.domain.AuthJWT;
import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.UseAuthToken;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.auth.model.AuthenticationRequest;
import com.lims.api.auth.model.AuthenticationResponse;
import com.lims.api.exception.domain.UnAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthTokenProvider authTokenProvider;
    private final AuthProperties authProperties;

    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> login(@Validated @RequestBody AuthenticationRequest request) throws UnAuthenticatedException {
        AuthToken authToken = authTokenProvider.generate(request.getUsername(), request.getPassword());

        ResponseCookie accessTokenCookie = ResponseCookie
                .from(authProperties.jwt.accessToken.cookie.name, authToken.getAccessToken())
                .maxAge(authProperties.jwt.accessToken.cookie.maxAge)
                .secure(authProperties.jwt.secure)
                .sameSite(authProperties.jwt.sameSite)
                .httpOnly(authProperties.jwt.httpOnly)
                .path("/")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(authProperties.jwt.refreshToken.cookie.name, authToken.getRefreshToken())
                .maxAge(authProperties.jwt.refreshToken.cookie.maxAge)
                .secure(authProperties.jwt.secure)
                .sameSite(authProperties.jwt.sameSite)
                .httpOnly(authProperties.jwt.httpOnly)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(AuthenticationResponse.of(authToken));
    }

    @PostMapping(value = "verification")
    public ResponseEntity<Boolean> verifyToken(@UseAuthToken AuthToken token) {
        return ResponseEntity.ok().body(authTokenProvider.verify(token.getAccessToken()));
    }

    @PostMapping(value = "token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@UseAuthToken AuthToken token) {
        return ResponseEntity.ok().body(AuthenticationResponse.of(authTokenProvider.refresh(token.getRefreshToken())));
    }

}