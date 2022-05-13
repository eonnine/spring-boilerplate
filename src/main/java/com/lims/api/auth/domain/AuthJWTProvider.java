package com.lims.api.auth.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthJWTProvider implements AuthTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthJwtProperties authProperties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
    );

    public AuthJWTProvider(AuthJwtProperties authProperties) {
        Algorithm algorithm = Algorithm.HMAC256(authProperties.jwt.secret);

        this.authProperties = authProperties;
        this.algorithm = algorithm;
        this.verifier = JWT.require(algorithm).withIssuer(authProperties.jwt.issuer).build();
    }

    @Override
    public AuthToken generate(String username, String password) {

        // TODO validation using user data

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
            logger.info("[{}] Token authentication failed. {}", this.getClass(), e.getMessage());
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
        // TODO add Custom Claims
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

    private Date getExpiresAt(AuthJwtProperties.Expire expire) {
        return dateOf(LocalDateTime.now()
                .plusDays(expire.days)
                .plusHours(expire.hours)
                .plusMinutes(expire.minutes)
                .plusSeconds(expire.seconds));
    }

    private Map<String, Object> getClaims() {
        return new HashMap<String, Object>();
    }

}