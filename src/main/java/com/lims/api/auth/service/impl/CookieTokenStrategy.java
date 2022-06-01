package com.lims.api.auth.service.impl;

import com.lims.api.auth.domain.CookieStrategyCondition;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.TokenStrategy;
import com.lims.api.common.properties.auth.AccessTokenProperties;
import com.lims.api.common.properties.auth.CookieProperties;
import com.lims.api.common.properties.auth.RefreshTokenProperties;
import com.lims.api.common.properties.auth.domain.TokenProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Service
@Conditional(CookieStrategyCondition.class)
@RequiredArgsConstructor
public class CookieTokenStrategy implements TokenStrategy {

    private final AccessTokenProperties accessTokenProperties;
    private final RefreshTokenProperties refreshTokenProperties;
    private final CookieProperties cookieProperties;

    @Override
    public Token findAccessToken(HttpServletRequest request) {
        return new Token(findTokenInCookies(request.getCookies(), accessTokenProperties.getName()));
    }

    @Override
    public Token findRefreshToken(HttpServletRequest request) {
        return  new Token(findTokenInCookies(request.getCookies(), refreshTokenProperties.getName()));
    }

    private String findTokenInCookies(Cookie[] cookies, String cookieName) {
        return Arrays.stream(Optional.ofNullable(cookies).orElseGet(() -> new Cookie[0]))
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }

    @Override
    public HttpHeaders makeResponseHeader(AuthToken authToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseCookie accessTokenCookie = makeResponseCookie(accessTokenProperties, authToken.getAccessToken());
        ResponseCookie refreshTokenCookie = makeResponseCookie(refreshTokenProperties, authToken.getRefreshToken());

        httpHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return httpHeaders;
    }

    private ResponseCookie makeResponseCookie(TokenProperty tokenProperty, Token token) {
        return ResponseCookie
                .from(tokenProperty.getName(), token.get())
                .maxAge(tokenProperty.getExpire().getMaxAge())
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite().getValue())
                .httpOnly(cookieProperties.isHttpOnly())
                .path(cookieProperties.getPath())
                .build();
    }
}
