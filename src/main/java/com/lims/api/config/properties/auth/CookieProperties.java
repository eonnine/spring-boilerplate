package com.lims.api.config.properties.auth;

import lombok.Getter;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "auth.token.cookie")
public class CookieProperties {
    private final String defaultPath = "/";
    private final boolean secure;
    private final boolean httpOnly;
    private final SameSiteCookies sameSite;
    private final String path;

    public CookieProperties(Boolean secure, Boolean httpOnly, SameSiteCookies sameSite, String path) {
        this.secure = secure == null ? false : secure;
        this.httpOnly = httpOnly == null ? true : httpOnly;
        this.sameSite = sameSite == null ? SameSiteCookies.STRICT : sameSite;
        this.path = path == null ? defaultPath : path;
    }
}