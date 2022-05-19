package com.lims.api.common.properties;

import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@ConstructorBinding
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    public final String authHeaderName = "authorization";
    public final String type;
    public final Strategy strategy;
    public final Jwt jwt;

    public AuthProperties(String type, Jwt jwt, Strategy strategy) {
        this.type = type == null ? "Bearer" : type;
        this.strategy = strategy == null ? Strategy.cookie : strategy;
        this.jwt = jwt;
    }

    public enum Strategy {
        header,
        cookie;

        public boolean isHeader() {
            return this.name() == Strategy.header.name();
        }
        public boolean isCookie() {
            return this.name() == Strategy.cookie.name();
        }
    }

    public static class Jwt {
        public final String issuer;
        public final String secret;
        public final Boolean secure;
        public final Boolean httpOnly;
        public final String sameSite;
        public final AccessToken accessToken;
        public final RefreshToken refreshToken;

        public Jwt(String issuer, String secret, Boolean secure, Boolean httpOnly, String sameSite, AccessToken accessToken, RefreshToken refreshToken) {
            this.issuer = issuer == null ? "lims" : issuer;
            this.secret = secret;
            this.secure = secure == null ? false : secure;
            this.httpOnly = httpOnly == null ? true : httpOnly;
            this.sameSite = sameSite == null ? SameSiteCookies.STRICT.getValue() : sameSite;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    public static class AccessToken {
        public final Expire expire;
        public final Cookie cookie;

        public AccessToken(Expire expire, Cookie cookie) {
            this.expire = expire;
            Long maxAge = expire == null ? 0L : expire.getMaxAge();

            if (cookie != null && cookie.name != null) {
                this.cookie = new Cookie(cookie.name, maxAge);
            } else  {
                this.cookie = new Cookie("access-token", maxAge);
            }
        }
    }

    public static class RefreshToken {
        public final Expire expire;
        public final Cookie cookie;

        public RefreshToken(Expire expire, Cookie cookie) {
            this.expire = expire;
            Long maxAge = expire == null ? 0L : expire.getMaxAge();

            if (cookie != null && cookie.name != null) {
                this.cookie = new Cookie(cookie.name, maxAge);
            } else  {
                this.cookie = new Cookie("refresh-token", maxAge);
            }
        }
    }

    public static class Expire {
        private final Long DefaultValue = 0L;

        public final Long days;
        public final Long hours;
        public final Long minutes;
        public final Long seconds;

        public Expire(Long days, Long hours, Long minutes, Long seconds) {
            this.days = days == null ? DefaultValue : days;
            this.hours = hours == null ? DefaultValue : hours;
            this.minutes = minutes == null ? DefaultValue : minutes;
            this.seconds = seconds == null ? DefaultValue : seconds;
        }

        private Long getMaxAge() {
            Long maxAge = 0L;
            if (isExistsValue(this.days)) {
                maxAge += this.days * 24L * 60L * 60L;
            }
            if (isExistsValue(this.hours)) {
                maxAge += this.hours * 60L * 60L;
            }
            if (isExistsValue(this.minutes)) {
                maxAge += this.minutes * 60L;
            }
            if (isExistsValue(this.seconds)) {
                maxAge += this.seconds;
            }
            return maxAge;
        }

        private boolean isExistsValue(Long value) {
            return !value.equals(DefaultValue);
        }
    }

    public static class Cookie {
        public final String name;
        public final Long maxAge;

        public Cookie(String name, Long maxAge) {
            this.name = name;
            this.maxAge = maxAge;
        }

    }

}