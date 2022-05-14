package com.lims.api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.server.Cookie;

import java.util.Map;

@Getter
@AllArgsConstructor
public class AuthToken {

    private final String accessToken;
    private final String refreshToken;

}