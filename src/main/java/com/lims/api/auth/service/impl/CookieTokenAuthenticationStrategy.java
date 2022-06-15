package com.lims.api.auth.service.impl;

import com.lims.api.auth.condition.DefaultOrCookieTokenStrategyCondition;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.condition.CookieTokenStrategyCondition;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.service.TokenAuthenticationStrategy;
import com.lims.api.config.properties.auth.AccessTokenProperties;
import com.lims.api.config.properties.auth.CookieProperties;
import com.lims.api.config.properties.auth.RefreshTokenProperties;
import com.lims.api.config.properties.auth.domain.TokenProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Service
@Conditional(DefaultOrCookieTokenStrategyCondition.class)
@RequiredArgsConstructor
public class CookieTokenAuthenticationStrategy implements TokenAuthenticationStrategy {

    private final AccessTokenProperties accessTokenProperties;
    private final RefreshTokenProperties refreshTokenProperties;
    private final CookieProperties cookieProperties;

    @Override
    public AuthToken getAuthenticationAt(HttpServletRequest request) {
        return AuthToken.builder()
                .accessToken(findAccessToken(request))
                .refreshToken(findRefreshToken(request))
                .build();
    }

    private Token findAccessToken(HttpServletRequest request) {
        return new Token(findTokenInCookies(request.getCookies(), accessTokenProperties.getName()));
    }

    private Token findRefreshToken(HttpServletRequest request) {
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