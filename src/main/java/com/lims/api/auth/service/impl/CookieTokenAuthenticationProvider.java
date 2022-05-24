package com.lims.api.auth.service.impl;

import com.lims.api.auth.service.TokenProvider;
import com.lims.api.common.properties.AuthProperties;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

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

    private String findTokenInCookies(Cookie[] cookies, String cookieName) {
        return Arrays.stream(Optional.ofNullable(cookies).orElseGet(() -> new Cookie[0]))
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }

}