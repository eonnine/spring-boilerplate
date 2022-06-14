package com.lims.api.config.properties.auth;

import com.lims.api.config.properties.auth.domain.TokenStrategyProperty;
import com.lims.api.config.properties.auth.domain.TokenProperty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "auth.token")
public class TokenProperties {
    private final String defaultSecret = "secret-key";
    private final String defaultIssuer = "lims";
    private final String secret;
    private final String issuer;
    private final String prefix;
    private final TokenProperty accessToken;
    private final TokenProperty refreshToken;
    private final CookieProperties cookie;
    private final TokenStrategyProperty strategy;

    public TokenProperties(String secret, String issuer, AccessTokenProperties accessToken, RefreshTokenProperties refreshToken, CookieProperties cookie, TokenStrategyProperty strategy) {
        this.secret = secret == null ? defaultSecret : secret;
        this.issuer = issuer == null ? defaultIssuer : issuer;
        this.accessToken = accessToken == null ? new AccessTokenProperties(null) : accessToken;
        this.refreshToken = refreshToken == null ? new RefreshTokenProperties(null) : refreshToken;
        this.cookie = cookie == null ? new CookieProperties(null, null, null, null) : cookie;

        TokenStrategyProperty finalStrategy = strategy == null ? TokenStrategyProperty.COOKIE : strategy;
        this.strategy = finalStrategy;
        this.prefix = finalStrategy.getPrefix();
    }
}