package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.properties.AuthProperties;
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
            ValidationResult validationResult = verifyResult(refreshToken);
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

    public final boolean verify(String token) {
        return verifyResult(token).isVerified();
    }

    @Override
    public ValidationResult verifyResult(String token) {
        try {
            if (Strings.isEmpty(token)) {
                return new ValidationResult(false, "error.auth.notFoundAuthorization");
            }

            verifier.verify(token);

            return new ValidationResult(true);

        } catch (JWTVerificationException e) {
            log.info("[{}] Failed to verify auth token. {}", this.getClass(), e.getMessage());
            return new ValidationResult(false, "error.auth.invalidToken");

        } catch (Exception e){
            e.printStackTrace();
            return new ValidationResult(false, "error.auth.default");
        }
    }

    protected String createToken(Date expiresAt) {
        return JWT.create()
                .withHeader(header)
                .withIssuer(authProperties.getJwt().getIssuer())
                .withNotBefore(dateOf(LocalDateTime.now()))
                .withIssuedAt(dateOf(LocalDateTime.now()))
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    };

    protected DecodedJWT decodeJWT(String token) {
        if (token == null) {
            return null;
        }
        return JWT.decode(token);
    }

    private final Date dateOf(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    protected final Algorithm createJWTAlgorithm(AuthProperties authProperties) {
        return algorithm == null ? Algorithm.HMAC256(authProperties.getJwt().getSecret()) : algorithm;
    }

    protected final JWTVerifier createJWTVerifier(AuthProperties authProperties) {
        return verifier == null ? JWT.require(createJWTAlgorithm(authProperties)).withIssuer(authProperties.getJwt().getIssuer()).build() : verifier;
    }

    protected final Date getExpiresAt(AuthProperties.Expire expire) {
        return dateOf(LocalDateTime.now()
                .plusDays(expire.getDays())
                .plusHours(expire.getHours())
                .plusMinutes(expire.getMinutes())
                .plusSeconds(expire.getSeconds()));
    }
}