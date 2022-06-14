package com.lims.api.config.properties.auth;

import com.lims.api.config.properties.auth.domain.TokenProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "auth.token.access-token")
public class AccessTokenProperties extends TokenProperty {
    private final String name = "access-token";

    public AccessTokenProperties(AccessTokenExpireProperties expire) {
        super(expire);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected AccessTokenExpireProperties getDefaultExpire() {
        return new AccessTokenExpireProperties(null, null, 30L, null);
    }
}