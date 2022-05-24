package com.lims.api.common.properties;

import lombok.Getter;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private final String authHeaderName = "authorization";
    private final String type;
    private final Strategy strategy;
    private final Jwt jwt;

    public AuthProperties(Jwt jwt, Strategy strategy) {
        if (strategy == null) {
            this.strategy = Strategy.COOKIE;
            this.type = null;
        } else {
            this.strategy = strategy;
            this.type = Strategy.COOKIE.name().equals(strategy.name().toUpperCase()) ? null : "Bearer";
        }
        this.jwt = jwt == null ? new Jwt(null, null, true, true, null, null, null, null) : jwt;
    }

    public enum Strategy {
        HEADER,
        COOKIE;

        public boolean isHeader() {
            return this.name() == Strategy.HEADER.name();
        }
        public boolean isCookie() {
            return this.name() == Strategy.COOKIE.name();
        }
    }

    @Getter
    public static class Jwt {
        private final String issuer;
        private final String secret;
        private final boolean secure;
        private final boolean httpOnly;
        private final String sameSite;
        private final String path;
        private final AccessToken accessToken;
        private final RefreshToken refreshToken;

        public Jwt(String issuer, String secret, Boolean secure, Boolean httpOnly, String sameSite, String path, AccessToken accessToken, RefreshToken refreshToken) {
            this.issuer = issuer == null ? "lims" : issuer;
            this.secret = secret == null ? "secret" : secret;
            this.secure = secure == null ? false : secure;
            this.httpOnly = httpOnly == null ? true : httpOnly;
            this.sameSite = sameSite == null ? SameSiteCookies.STRICT.getValue() : sameSite;
            this.path = path == null ? "/" : path;
            this.accessToken = accessToken == null ? new AccessToken(null, null) : accessToken;
            this.refreshToken = refreshToken == null ? new RefreshToken(null, null) : refreshToken;
        }
    }

    public interface AuthToken {
        public Expire getExpire();
        public Cookie getCookie();
    }

    public static class AccessToken implements AuthToken {
        private final Expire expire;
        private final Cookie cookie;

        public AccessToken(Expire expire, Cookie cookie) {
            this.expire = expire == null ? new Expire(null, null, 30L, null) : expire;
            Long maxAge = expire == null ? 0L : expire.getMaxAge();

            if (cookie != null && cookie.name != null) {
                this.cookie = new Cookie(cookie.name, maxAge);
            } else  {
                this.cookie = new Cookie("access-token", maxAge);
            }
        }

        @Override
        public Expire getExpire() {
            return this.expire;
        }

        @Override
        public Cookie getCookie() {
            return this.cookie;
        }
    }

    public static class RefreshToken implements AuthToken {
        private final Expire expire;
        private final Cookie cookie;

        public RefreshToken(Expire expire, Cookie cookie) {
            this.expire = expire == null ? new Expire(null, 2L, null, null) : expire;
            Long maxAge = expire == null ? 0L : expire.getMaxAge();

            if (cookie != null && cookie.name != null) {
                this.cookie = new Cookie(cookie.name, maxAge);
            } else  {
                this.cookie = new Cookie("refresh-token", maxAge);
            }
        }

        @Override
        public Expire getExpire() {
            return this.expire;
        }

        @Override
        public Cookie getCookie() {
            return this.cookie;
        }
    }

    @Getter
    public static class Expire {
        private final Long DefaultValue = 0L;

        private final Long days;
        private final Long hours;
        private final Long minutes;
        private final Long seconds;

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

    @Getter
    public static class Cookie {
        private final String name;
        private final Long maxAge;

        public Cookie(String name, Long maxAge) {
            this.name = name;
            this.maxAge = maxAge;
        }

    }

}