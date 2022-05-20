package com.lims.api.auth.service.impl;

import com.lims.api.common.properties.AuthProperties;
import com.lims.api.auth.service.AuthTokenProvider;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class AuthTokenProviderFactoryBean extends AbstractFactoryBean<AuthTokenProvider> {

    private final AuthProperties authProperties;

    public AuthTokenProviderFactoryBean(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public Class<?> getObjectType() {
        return AuthTokenProvider.class;
    }

    @Override
    protected AuthTokenProvider createInstance() {
        if (authProperties.getStrategy().isCookie()) {
            return new CookieAuthTokenProvider(authProperties);
        }
        else if (authProperties.getStrategy().isHeader()) {
            return new HeaderAuthTokenProvider(authProperties);
        }

        return null;
    }
}