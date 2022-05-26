package com.lims.api.common.properties.auth;

import com.lims.api.common.properties.auth.domain.AuthStrategyProperty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private final AuthStrategyProperty strategy;
    private final TokenProperties token;
    private final CookieProperties cookie;

    public AuthProperties(AuthStrategyProperty strategy, TokenProperties token, CookieProperties cookie) {
        this.strategy = strategy == null ? AuthStrategyProperty.TOKEN : strategy;
        this.token = token == null ? new TokenProperties(null, null, null, null, null) : token;
        this.cookie = cookie == null ? new CookieProperties(null, null, null, null) : cookie;
    }
}