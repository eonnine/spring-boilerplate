package com.lims.api.common.properties.auth;

import com.lims.api.common.properties.auth.domain.TokenProperty;
import com.lims.api.common.properties.auth.domain.TokenTypeProperty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "auth.token")
public class TokenProperties {
    private final String defaultIssuer = "lims";
    private final String issuer;
    private final TokenTypeProperty type;
    private final TokenProperty accessToken;
    private final TokenProperty refreshToken;

    public TokenProperties(String issuer, TokenTypeProperty type, AccessTokenProperties accessToken, RefreshTokenProperties refreshToken) {
        this.issuer = issuer == null ? defaultIssuer : issuer;
        this.type = type == null ? TokenTypeProperty.COOKIE : type;
        this.accessToken = accessToken == null ? new AccessTokenProperties(null) : accessToken;
        this.refreshToken = refreshToken == null ? new RefreshTokenProperties(null) : refreshToken;
    }
}