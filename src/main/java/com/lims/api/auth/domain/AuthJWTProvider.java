package com.lims.api.auth.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthJWTProvider implements AuthTokenProvider {

    private final AuthJwtProperties authProperties;

    @Override
    public AuthToken generate(String username, String password) {
        try {

            Date accessTokenExpiresAt = getExpiresAt(authProperties.jwt.accessToken.expire);
            Date refreshTokenExpiresAt = getExpiresAt(authProperties.jwt.refreshToken.expire);

            return AuthJWT.builder()
                    .accessToken(createToken(accessTokenExpiresAt))
                    .refreshToken(createToken(refreshTokenExpiresAt))
                    .build();

        } catch (JWTCreationException e) {
            e.printStackTrace();
            return AuthJWT.builder().build();
        }
    }

    @Override
    public boolean verify(AuthToken token) {
        return false;
    }

    @Override
    public AuthToken refresh(AuthToken token) {
        return null;
    }

    private String createToken(Date expiresAt) {
        // TODO add Claims
        return JWT.create()
                .withIssuer(authProperties.jwt.issuer)
                .withExpiresAt(expiresAt)
                .sign(getJWTAlgorithm());
    }

    private Algorithm getJWTAlgorithm() {
        return Algorithm.HMAC256(authProperties.jwt.secret);
    }

    private Date getExpiresAt(AuthJwtProperties.Expire expire) {
        return Date.from(LocalDateTime.now()
                .plusDays(expire.days)
                .plusHours(expire.hours)
                .plusMinutes(expire.minutes)
                .plusSeconds(expire.seconds)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private Map<String, Object> getClaims() {
        return new HashMap<String, Object>();
    }

}