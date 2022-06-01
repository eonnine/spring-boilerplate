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

    public AuthProperties(AuthStrategyProperty strategy, TokenProperties token) {
        this.strategy = strategy == null ? AuthStrategyProperty.TOKEN : strategy;
        this.token = token == null ? getDefaultToken() : token;
    }

    private TokenProperties getDefaultToken() {
       return new TokenProperties(null, null, null, null, null, null);
    }
}