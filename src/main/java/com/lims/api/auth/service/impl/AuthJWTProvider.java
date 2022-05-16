package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lims.api.auth.domain.AuthJWT;
import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.exception.domain.UnAuthenticatedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class AuthJWTProvider implements AuthTokenProvider {

    private final AuthProperties.Strategy strategy;
    private final AuthProperties authProperties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
    );

    public AuthJWTProvider(AuthProperties authProperties) {
        Algorithm algorithm = Algorithm.HMAC256(authProperties.jwt.secret);

        this.strategy = authProperties.strategy;
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
            // TODO input user claims

            return AuthJWT.builder()
                    .accessToken(generateAccessToken())
                    .refreshToken(generateRefreshToken())
                    .build();

        } catch (UnAuthenticatedException e) {
            log.info("[{}] User Authenticate User", this.getClass());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return AuthJWT.builder().build();
        }
    }

    @Override
    public boolean verify(String token) {
        try {
            if (Strings.isEmpty(token)) {
                return false;
            }

            if (strategy.isHeader()) {
                String[] bearerTokens = token.split(" ");

                if (!authProperties.type.equals(bearerTokens[0])) {
                    log.info("[{}] Invalid token type.", this.getClass());
                    return false;
                }
                token = bearerTokens[1];
            }

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
    public AuthToken refresh(String refreshToken) throws UnAuthenticatedException {
        try {
            if (refreshToken == null || !verify(refreshToken)) {
                throw new UnAuthenticatedException("error.auth.invalidToken");
            }

            DecodedJWT jwt = decodeJWT(refreshToken);

            // TODO input user claims

            return AuthJWT.builder()
                    .accessToken(generateAccessToken())
                    .refreshToken(generateRefreshToken())
                    .build();
        } catch(UnAuthenticatedException e) {
            log.info("[{}] Failed to refresh issue auth token. {}", this.getClass(), e.getMessage());
            throw e;
        } catch(Exception e) {
            e.printStackTrace();
            return AuthJWT.builder().build();
        }
    }

    private String generateAccessToken() {
        Date accessTokenExpiresAt = getExpiresAt(authProperties.jwt.accessToken.expire);
        return strategy.isCookie() ? createToken(accessTokenExpiresAt) : createBearerToken(accessTokenExpiresAt);
    }

    private String generateRefreshToken() {
        Date refreshTokenExpiresAt = getExpiresAt(authProperties.jwt.refreshToken.expire);
        return strategy.isCookie() ? createToken(refreshTokenExpiresAt) : createBearerToken(refreshTokenExpiresAt);
    }

    private String createBearerToken(Date expiresAt) {
        return authProperties.type + " " + createToken(expiresAt);
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

    private DecodedJWT decodeJWT(String token) {
        if (token == null) {
            return null;
        }

        String jwt = strategy.isCookie() ? token : token.split(" ")[0];

        return JWT.decode(jwt);
    }

}