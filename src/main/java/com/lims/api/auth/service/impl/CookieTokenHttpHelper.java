package com.lims.api.auth.service.impl;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.TokenHttpHelper;
import com.lims.api.common.properties.auth.AuthProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CookieTokenHttpHelper implements TokenHttpHelper {

    private AuthProperties authProperties;

    public CookieTokenHttpHelper(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public ResponseEntity toTokenResponseEntity(HttpStatus status, AuthToken authToken) {
        return ResponseEntity
                .status(status)
                .headers(makeHttpHeaders(authToken))
                .body(null);
    }

    private HttpHeaders makeHttpHeaders(AuthToken authToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
//        ResponseCookie accessTokenCookie = makeResponseCookie(authProperties.getJwt().getAccessToken(), authToken.getAccessToken());
//        ResponseCookie refreshTokenCookie = makeResponseCookie(authProperties.getJwt().getRefreshToken(), authToken.getRefreshToken());

//        httpHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
//        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return httpHeaders;
    }

//    private ResponseCookie makeResponseCookie(AuthProperties.AuthToken authTokenProperty, String token) {
//        return ResponseCookie
//                .from(authTokenProperty.getCookie().getName(), token)
//                .maxAge(authTokenProperty.getCookie().getMaxAge())
//                .secure(authProperties.getJwt().isSecure())
//                .sameSite(authProperties.getJwt().getSameSite())
//                .httpOnly(authProperties.getJwt().isHttpOnly())
//                .path(authProperties.getJwt().getPath())
//                .build();
//        return null;
//    }
}