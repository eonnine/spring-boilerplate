package com.lims.api.auth.service.impl;

import com.lims.api.auth.service.TokenProvider;
import com.lims.api.common.properties.AuthProperties;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class CookieTokenAuthenticationProvider extends RegularTokenAuthenticationProvider {

    private AuthProperties authProperties;

    public CookieTokenAuthenticationProvider(AuthProperties authProperties, TokenProvider authTokenProvider) {
        super(authProperties, authTokenProvider);
        this.authProperties = authProperties;
    }

    @Override
    public String getAccessToken(HttpServletRequest request) {
        return findTokenInCookies(request.getCookies(), authProperties.getJwt().getAccessToken().getCookie().getName());
    }

    @Override
    public String getRefreshToken(HttpServletRequest request) {
        return findTokenInCookies(request.getCookies(), authProperties.getJwt().getRefreshToken().getCookie().getName());
    }

    @SuppressWarnings("ConstantConditions")
    private String findTokenInCookies(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElseGet(() -> null);
    }

    private ResponseCookie makeResponseCookie(AuthProperties.AuthToken authTokenProperty, String token) {
        return ResponseCookie
                .from(authTokenProperty.getCookie().getName(), token)
                .maxAge(authTokenProperty.getCookie().getMaxAge())
                .secure(authProperties.getJwt().isSecure())
                .sameSite(authProperties.getJwt().getSameSite())
                .httpOnly(authProperties.getJwt().isHttpOnly())
                .path(authProperties.getJwt().getPath())
                .build();
    }
}
