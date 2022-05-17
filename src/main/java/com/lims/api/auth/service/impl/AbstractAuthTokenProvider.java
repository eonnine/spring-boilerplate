package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.common.domain.ValidationResult;
import com.lims.api.exception.domain.UnAuthenticatedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Slf4j
public abstract class AbstractAuthTokenProvider implements AuthTokenProvider {

    private final AuthProperties authProperties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
    );

    public AbstractAuthTokenProvider(AuthProperties authProperties) {
        this.authProperties = authProperties;
        this.algorithm = createJWTAlgorithm(authProperties);
        this.verifier = createJWTVerifier(authProperties);
    }

    public abstract String generateAccessToken();

    public abstract String generateRefreshToken();

    @Override
    public abstract AuthToken getAuthToken(HttpServletRequest request);

    @Override
    public AuthToken generate(String username, String password) {
        try {
            if (username == null || password == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "error.auth.notFoundAuthorization");
            }
            // TODO validation using user data
            if (username.equals("ERROR")) {
                throw new UnAuthenticatedException("error.auth.unauthenticated");
            }

            // TODO input user claims

            return AuthToken.builder()
                    .accessToken(generateAccessToken())
                    .refreshToken(generateRefreshToken())
                    .build();

        } catch (UnAuthenticatedException | HttpClientErrorException e) {
            log.info("[{}] Failed to authenticate user", this.getClass());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    };

    @Override
    public AuthToken refresh(String refreshToken) {
        try {
            ValidationResult validationResult = verify(refreshToken);
            if (refreshToken == null || !validationResult.isVerified()) {
                throw new UnAuthenticatedException("error.auth.invalidToken");
            }

            // TODO input user claims

            return AuthToken.builder()
                    .accessToken(generateAccessToken())
                    .refreshToken(generateRefreshToken())
                    .build();

        } catch(UnAuthenticatedException e) {
            log.info("[{}] Failed to refresh issue auth token. {}", this.getClass(), e.getMessage());
            throw e;
        } catch(Exception e) {
            e.printStackTrace();
            return AuthToken.builder().build();
        }
    };

    @Override
    public ValidationResult verify(String token) {
        try {
            if (Strings.isEmpty(token)) {
                return ValidationResult.builder().verified(false).build();
            }
            if (isBearerToken(token)) {
                token = token.split(" ")[1];
            }
            verifier.verify(token);

            return ValidationResult.builder().verified(true).build();

        } catch (JWTVerificationException e) {
            log.info("[{}] Failed to verify auth token. {}", this.getClass(), e.getMessage());
            return ValidationResult.builder().verified(false).build();

        } catch (Exception e){
            e.printStackTrace();
            return ValidationResult.builder().verified(false).build();
        }
    }

    protected String createToken(Date expiresAt) {
        return JWT.create()
                .withHeader(header)
                .withIssuer(authProperties.jwt.issuer)
                .withNotBefore(dateOf(LocalDateTime.now()))
                .withIssuedAt(dateOf(LocalDateTime.now()))
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    };

    protected DecodedJWT decodeJWT(String token) {
        if (token == null) {
            return null;
        }
        String jwt = isBearerToken(token) ? token.split(" ")[1] : token;
        return JWT.decode(jwt);
    }

    private final boolean isBearerToken(String token) {
        return !Strings.isEmpty(token) && authProperties.type.equals(token.split(" ")[0]);
    }

    private final Date dateOf(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    protected final Algorithm createJWTAlgorithm(AuthProperties authProperties) {
        return algorithm == null ? Algorithm.HMAC256(authProperties.jwt.secret) : algorithm;
    }

    protected final JWTVerifier createJWTVerifier(AuthProperties authProperties) {
        return verifier == null ? JWT.require(createJWTAlgorithm(authProperties)).withIssuer(authProperties.jwt.issuer).build() : verifier;
    }

    protected final Date getExpiresAt(AuthProperties.Expire expire) {
        return dateOf(LocalDateTime.now()
                .plusDays(expire.days)
                .plusHours(expire.hours)
                .plusMinutes(expire.minutes)
                .plusSeconds(expire.seconds));
    }
}