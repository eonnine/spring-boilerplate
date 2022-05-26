package com.lims.api.auth.service.impl;

import com.lims.api.auth.domain.CookieStrategyCondition;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.properties.auth.TokenProperties;
import com.lims.api.common.properties.auth.domain.TokenProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Service
@Conditional(CookieStrategyCondition.class)
public class CookieTokenAuthenticationService extends RegularTokenAuthenticationService {

    private final TokenProperty accessTokenProperties;
    private final TokenProperty refreshTokenProperties;

    public CookieTokenAuthenticationService(TokenProperties tokenProperties, TokenService tokenService) {
        super(tokenProperties, tokenService);
        this.accessTokenProperties = tokenProperties.getAccessToken();
        this.refreshTokenProperties = tokenProperties.getRefreshToken();
    }

    @Override
    protected Token getAccessToken(HttpServletRequest request) {
        return new Token(findTokenInCookies(request.getCookies(), accessTokenProperties.getName()));
    }

    @Override
    protected Token getRefreshToken(HttpServletRequest request) {
        return new Token(findTokenInCookies(request.getCookies(), refreshTokenProperties.getName()));
    }

    private String findTokenInCookies(Cookie[] cookies, String cookieName) {
        return Arrays.stream(Optional.ofNullable(cookies).orElseGet(() -> new Cookie[0]))
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElse(null);
    }
}
