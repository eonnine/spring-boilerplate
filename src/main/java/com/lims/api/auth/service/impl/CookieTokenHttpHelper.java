package com.lims.api.auth.service.impl;

import com.lims.api.auth.domain.CookieStrategyCondition;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.TokenHttpHelper;
import com.lims.api.common.properties.auth.CookieProperties;
import com.lims.api.common.properties.auth.TokenProperties;
import com.lims.api.common.properties.auth.domain.TokenProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Conditional(CookieStrategyCondition.class)
public class CookieTokenHttpHelper implements TokenHttpHelper {

    private final TokenProperties tokenProperties;
    private final CookieProperties cookieProperties;

    @Override
    public ResponseEntity toResponseEntity(HttpStatus status, AuthToken authToken) {
        return ResponseEntity
                .status(status)
                .headers(makeHttpHeaders(authToken))
                .body(null);
    }

    private HttpHeaders makeHttpHeaders(AuthToken authToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseCookie accessTokenCookie = makeResponseCookie(tokenProperties.getAccessToken(), authToken.getAccessToken());
        ResponseCookie refreshTokenCookie = makeResponseCookie(tokenProperties.getRefreshToken(), authToken.getRefreshToken());

        httpHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return httpHeaders;
    }

    private ResponseCookie makeResponseCookie(TokenProperty tokenProperty, Token token) {
        return ResponseCookie
                .from(tokenProperty.getName(), token.get())
                .maxAge(tokenProperty.getExpire().getMaxAge())
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .httpOnly(cookieProperties.isHttpOnly())
                .path(cookieProperties.getPath())
                .build();
    }
}