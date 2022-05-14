package com.lims.api.auth.controller;

import com.lims.api.auth.domain.AuthJWT;
import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.AuthTokenProvider;
import com.lims.api.auth.model.AuthenticationRequest;
import com.lims.api.auth.model.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthTokenProvider authTokenProvider;
    private final AuthProperties authProperties;

    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> login(@Validated @RequestBody AuthenticationRequest request) {
        AuthToken authToken = authTokenProvider.generate(request.getUsername(), request.getPassword());

        ResponseCookie accessTokenCookie = ResponseCookie
                .from(authProperties.jwt.accessToken.cookie.name, authToken.getAccessToken())
                .maxAge(authProperties.jwt.accessToken.cookie.maxAge)
                .secure(authProperties.jwt.secure)
                .sameSite(Cookie.SameSite.STRICT.attributeValue())
                .httpOnly(true)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(authProperties.jwt.refreshToken.cookie.name, authToken.getRefreshToken())
                .maxAge(authProperties.jwt.refreshToken.cookie.maxAge)
                .secure(authProperties.jwt.secure)
                .sameSite(Cookie.SameSite.STRICT.attributeValue())
                .httpOnly(true)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(AuthenticationResponse.of(authToken));
    }

    @PostMapping(value = "verification")
    public ResponseEntity<Boolean> verifyToken(@RequestBody AuthJWT token, HttpServletRequest request) {
        return ResponseEntity.ok().body(authTokenProvider.verify(token.getAccessToken()));
    }

    @PostMapping(value = "token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody AuthJWT token) {
        return ResponseEntity.ok().body(AuthenticationResponse.of(authTokenProvider.refresh(token)));
    }

}