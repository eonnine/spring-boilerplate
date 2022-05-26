package com.lims.api.common.properties.auth;

import com.lims.api.common.properties.auth.domain.ExpireProperty;
import com.lims.api.common.properties.auth.domain.TokenProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "auth.token.refresh-token")
public class RefreshTokenProperties extends TokenProperty {
    private final String name = "refresh-token";

    public RefreshTokenProperties(ExpireProperty expire) {
        super(expire);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected RefreshTokenExpireProperties getDefaultExpire() {
        return new RefreshTokenExpireProperties(null, 2L, null, null);
    }
}