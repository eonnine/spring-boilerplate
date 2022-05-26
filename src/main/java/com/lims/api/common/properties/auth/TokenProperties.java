package com.lims.api.common.properties.auth;

import com.lims.api.common.properties.auth.domain.TokenProperty;
import com.lims.api.common.properties.auth.domain.TokenTypeProperty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Optional;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "auth.token")
public class TokenProperties {
    private final String defaultSecret = "secret-key";
    private final String defaultIssuer = "lims";
    private final String secret;
    private final String issuer;
    private final String type;
    private final TokenProperty accessToken;
    private final TokenProperty refreshToken;

    public TokenProperties(String secret, String issuer, TokenTypeProperty type, AccessTokenProperties accessToken, RefreshTokenProperties refreshToken) {
        this.secret = secret == null ? defaultSecret : secret;
        this.issuer = issuer == null ? defaultIssuer : issuer;
        this.type = getTokenTypePrefix(type);
        this.accessToken = accessToken == null ? new AccessTokenProperties(null) : accessToken;
        this.refreshToken = refreshToken == null ? new RefreshTokenProperties(null) : refreshToken;
    }

    private String getTokenTypePrefix(TokenTypeProperty type) {
        return Optional.ofNullable(type)
                .map(TokenTypeProperty::getPrefix)
                .orElseGet(() -> "");
    }
}