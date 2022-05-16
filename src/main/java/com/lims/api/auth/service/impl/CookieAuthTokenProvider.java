package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.common.domain.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
public class CookieAuthTokenProvider extends AbstractAuthTokenProvider {

    private final AuthProperties authProperties;

    public CookieAuthTokenProvider(AuthProperties authProperties) {
        super(authProperties);
        this.authProperties = authProperties;
    }

    @Override
    public String generateAccessToken() {
        Date tokenExpiresAt = getExpiresAt(authProperties.jwt.accessToken.expire);
        return this.createToken(tokenExpiresAt);
    }

    @Override
    public String generateRefreshToken() {
        Date tokenExpiresAt = getExpiresAt(authProperties.jwt.refreshToken.expire);
        return this.createToken(tokenExpiresAt);
    }

    @Override
    public ValidationResult verify(String token) {
        try {
            if (Strings.isEmpty(token)) {
                return ValidationResult.builder()
                        .verified(false)
                        .messageCode("error.auth.notFoundAuthorization")
                        .build();
            }

            JWTVerifier verifier = createJWTVerifier(authProperties);
            verifier.verify(token);

            return ValidationResult.builder().verified(true).build();

        } catch (JWTVerificationException e) {
            log.info("[{}] Failed to verify auth token. {}", this.getClass(), e.getMessage());
            return ValidationResult.builder()
                    .verified(false)
                    .messageCode("error.auth.invalidToken")
                    .build();

        } catch (Exception e){
            e.printStackTrace();
            return ValidationResult.builder()
                    .verified(false)
                    .messageCode("error.auth.default")
                    .build();
        }
    }

    @Override
    public AuthToken getAuthToken(HttpServletRequest request) {
        if (request == null) {
            return AuthToken.builder().build();
        }

        String accessToken = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (authProperties.jwt.accessToken.cookie.name.equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
                else if (authProperties.jwt.refreshToken.cookie.name.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}