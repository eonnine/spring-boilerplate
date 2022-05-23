package com.lims.api.auth.service.impl;

import com.lims.api.auth.service.TokenAuthenticationProvider;
import com.lims.api.auth.service.TokenHttpHelper;
import com.lims.api.auth.service.TokenProvider;
import com.lims.api.common.properties.AuthProperties;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthenticationFactory {

    private AuthProperties authProperties;
    private TokenProvider tokenProvider;

    public AuthenticationFactory(AuthProperties authProperties, TokenProvider tokenProvider) {
        this.authProperties = authProperties;
        this.tokenProvider = tokenProvider;
    }

    public TokenAuthenticationProvider createTokenAuthenticationProvider() {
        if (authProperties.getStrategy().isCookie()) {
            return new CookieTokenAuthenticationProvider(authProperties, tokenProvider);
        }
        if (authProperties.getStrategy().isHeader()) {
            return new HeaderTokenAuthenticationProvider(authProperties, tokenProvider);
        }
        log.error("[{}] Not created tokenAuthenticationProvider", this.getClass());
        return null;
    }

    public TokenHttpHelper createTokenHttpHelper() {
        if (authProperties.getStrategy().isCookie()) {
            return new CookieTokenHttpHelper(authProperties);
        }
        if (authProperties.getStrategy().isHeader()) {
            return new HeaderTokenHttpHelper(authProperties);
        }
        log.error("[{}] Not created tokenHttpHelper", this.getClass());
        return null;
    }
}
