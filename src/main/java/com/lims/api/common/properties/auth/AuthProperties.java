package com.lims.api.common.properties.auth;

import com.lims.api.common.properties.auth.domain.StrategyProperty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private final StrategyProperty strategy;
    private final TokenProperties token;
    private final CookieProperties cookie;

    public AuthProperties(StrategyProperty strategy, TokenProperties token, CookieProperties cookie) {
        this.strategy = strategy == null ? StrategyProperty.COOKIE : strategy;
        this.token = token == null ? new TokenProperties(null,null, null, null, null) : token;
        this.cookie = cookie == null ? new CookieProperties(null, null, null, null) : cookie;
    }
}