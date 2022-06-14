package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.service.TokenService;
import com.lims.api.config.properties.auth.TokenProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class JWTService implements TokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final String prefix;
    private final Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
    );

    public JWTService(TokenProperties tokenProperties) {
        Algorithm algorithm = Algorithm.HMAC256(tokenProperties.getSecret());
        String issuer = tokenProperties.getIssuer();

        this.algorithm = algorithm;
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
        this.issuer = tokenProperties.getIssuer();
        this.prefix = tokenProperties.getPrefix();
    }

    @Override
    public final Token createToken(Date expiresAt) {
        String token = addPrefix(
                JWT.create()
                .withHeader(header)
                .withIssuer(issuer)
                .withNotBefore(nowDate())
                .withIssuedAt(nowDate())
                .withExpiresAt(expiresAt)
                .sign(algorithm)
        );
        return new Token(token);
    }

    @Override
    public boolean verify(Token token) {
        try {
            String jwt = token.get();

            if (Strings.isEmpty(jwt)) {
                throw new IllegalArgumentException();
            }

            verifier.verify(removePrefix(jwt));
            return true;

        } catch(JWTVerificationException e) {
            log.error("[{}] Failed to verify auth token. {}", this.getClass(), e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("[{}] Not found token.", this.getClass());
            return false;
        } catch (Exception e){
            log.error("[{}] Throw Exception during verify authentication. {}", this.getClass(), e.getMessage());
            return false;
        }
    }

    private String addPrefix(String value) {
        return prefix + value;
    }

    private String removePrefix(String value) {
        return Optional.ofNullable(value)
                .map(s -> s.split(prefix)[1])
                .orElse(value);
    }

    private Date nowDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}