package com.lims.api.auth.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "auth")
@RequiredArgsConstructor
public class AuthJwtProperties {

    public final Jwt jwt;

    @RequiredArgsConstructor
    static class Jwt {
        public final String issuer = "lims";
        public final String secret;
        public final AccessToken accessToken;
        public final RefreshToken refreshToken;
    }

    @RequiredArgsConstructor
    static class AccessToken {
        public final Expire expire;
    }

    @RequiredArgsConstructor
    static class RefreshToken {
        public final Expire expire;
    }

    @RequiredArgsConstructor
    static class Expire {
        public final Long days = 0L;
        public final Long hours = 0L;
        public final Long minutes = 0L;
        public final Long seconds = 0L;
    }

}