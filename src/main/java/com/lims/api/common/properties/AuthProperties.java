package com.lims.api.common.properties;

import lombok.Getter;
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
            this.secret = secret;
            this.secure = secure == null ? false : secure;
            this.httpOnly = httpOnly == null ? true : httpOnly;
            this.sameSite = sameSite == null ? SameSiteCookies.STRICT.getValue() : sameSite;
            this.path = path == null ? "/" : path;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    public interface AuthTokenProperty {
        public Expire getExpire();
        public Cookie getCookie();
    }

    public static class AccessToken implements AuthTokenProperty {
        private final Expire expire;
        private final Cookie cookie;

        public AccessToken(Expire expire, Cookie cookie) {
            this.expire = expire;
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

    public static class RefreshToken implements AuthTokenProperty {
        private final Expire expire;
        private final Cookie cookie;

        public RefreshToken(Expire expire, Cookie cookie) {
            this.expire = expire;
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