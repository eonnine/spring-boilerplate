package com.lims.api.auth.service.impl;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.properties.AuthProperties;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
public class CookieAuthTokenProvider extends AbstractAuthTokenProvider {

    private final AuthProperties authProperties;

    public CookieAuthTokenProvider(AuthProperties authProperties) {
        super(authProperties);
        this.authProperties = authProperties;
    }

    @Override
    public String generateAccessToken() {
        Date tokenExpiresAt = getExpiresAt(authProperties.getJwt().getAccessToken().getExpire());
        return this.createToken(tokenExpiresAt);
    }

    @Override
    public String generateRefreshToken() {
        Date tokenExpiresAt = getExpiresAt(authProperties.getJwt().getRefreshToken().getExpire());
        return this.createToken(tokenExpiresAt);
    }

    @Override
    public AuthToken getAuthToken(HttpServletRequest request) {
        if (request == null) {
            return AuthToken.builder().build();
        }

        String accessToken = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (authProperties.getJwt().getAccessToken().getCookie().getName().equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
                else if (authProperties.getJwt().getRefreshToken().getCookie().getName().equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}