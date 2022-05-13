package com.lims.api.auth.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "auth")
@RequiredArgsConstructor
public class AuthJwtProperties {

    public final Jwt jwt;

    public static class Jwt {
        public final String issuer;
        public final String secret;
        public final AccessToken accessToken;
        public final RefreshToken refreshToken;

        public Jwt(String issuer, String secret, AccessToken accessToken, RefreshToken refreshToken) {
            this.issuer = issuer == null ? "lims" : issuer;
            this.secret = secret;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @RequiredArgsConstructor
    public static class AccessToken {
        public final Expire expire;
    }

    @RequiredArgsConstructor
    public static class RefreshToken {
        public final Expire expire;
    }

    public static class Expire {
        public final Long days;
        public final Long hours;
        public final Long minutes;
        public final Long seconds;

        public Expire(Long days, Long hours, Long minutes, Long seconds) {
            this.days = days == null ? 0L : days;
            this.hours = hours == null ? 0L : hours;
            this.minutes = minutes == null ? 0L : minutes;
            this.seconds = seconds == null ? 0L : seconds;
        }
    }
}