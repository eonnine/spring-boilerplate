package com.lims.api.auth.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lims.api.exception.domain.UnAuthenticatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class AuthJWTProvider implements AuthTokenProvider {

    private final AuthProperties authProperties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
    );

    public AuthJWTProvider(AuthProperties authProperties) {
        Algorithm algorithm = Algorithm.HMAC256(authProperties.jwt.secret);

        this.authProperties = authProperties;
        this.algorithm = algorithm;
        this.verifier = JWT.require(algorithm).withIssuer(authProperties.jwt.issuer).build();
    }

    @Override
    public AuthToken generate(String username, String password) throws UnAuthenticatedException {

        // TODO validation using user data
        if (username.equals("ERROR")) {
            throw new UnAuthenticatedException("error.auth.unauthenticated");
        }

        try {
            Date accessTokenExpiresAt = getExpiresAt(authProperties.jwt.accessToken.expire);
            Date refreshTokenExpiresAt = getExpiresAt(authProperties.jwt.refreshToken.expire);

            return AuthJWT.builder()
                    .accessToken(createToken(accessTokenExpiresAt))
                    .refreshToken(createToken(refreshTokenExpiresAt))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return AuthJWT.builder().build();
        }
    }

    @Override
    public boolean verify(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.info("[{}] Failed to verify auth token. {}", this.getClass(), e.getMessage());
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public AuthToken refresh(AuthToken authToken) {
        if (verify(authToken.getRefreshToken())) {
            // TODO generate new AuthToken
            return AuthJWT.builder().build();
        }

        return AuthJWT.builder().build();
    }

    private String createToken(Date expiresAt) {
        return JWT.create()
                .withHeader(header)
                .withIssuer(authProperties.jwt.issuer)
                .withNotBefore(dateOf(LocalDateTime.now()))
                .withIssuedAt(dateOf(LocalDateTime.now()))
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    private Date dateOf(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date getExpiresAt(AuthProperties.Expire expire) {
        return dateOf(LocalDateTime.now()
                .plusDays(expire.days)
                .plusHours(expire.hours)
                .plusMinutes(expire.minutes)
                .plusSeconds(expire.seconds));
    }

}