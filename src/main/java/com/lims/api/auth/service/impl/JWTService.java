package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.i18n.service.LocaleMessageSource;
import com.lims.api.common.properties.auth.TokenProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JWTService implements TokenService {

    private final LocaleMessageSource messageSource;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final String tokenPrefix;
    private final Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
    );

    public JWTService(LocaleMessageSource messageSource, TokenProperties tokenProperties) {
        Algorithm algorithm = Algorithm.HMAC256(tokenProperties.getSecret());
        String issuer = tokenProperties.getIssuer();

        this.messageSource = messageSource;
        this.algorithm = algorithm;
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
        this.issuer = tokenProperties.getIssuer();
        this.tokenPrefix = tokenProperties.getType();
    }

    @Override
    public final Token createToken(Date expiresAt) {
        return new Token(
                tokenPrefix +
                JWT.create()
                .withHeader(header)
                .withIssuer(issuer)
                .withNotBefore(nowDate())
                .withIssuedAt(nowDate())
                .withExpiresAt(expiresAt)
                .sign(algorithm)
        );
    }

    @Override
    public final boolean verify(Token token) {
        return verifyResult(token).isVerified();
    }

    @Override
    public ValidationResult verifyResult(Token token) {
        try {
            String jwt = token.getToken();
            if (Strings.isEmpty(jwt)) {
                throw new UnAuthenticatedException("error.auth.notFoundAuthorization");
            }
            verifier.verify(jwt);
            return new ValidationResult(true);

        } catch(JWTVerificationException e) {
            log.error("[{}] Failed to verify auth token. {}", this.getClass(), e.getMessage());
            return new ValidationResult(false, e.getMessage());

        } catch (UnAuthenticatedException e) {
            String message = messageSource.getMessage(e.getMessageCode());
            log.error("[{}] Not found token. {}", this.getClass(), message);
            return new ValidationResult(false, message);

        } catch (Exception e){
            log.error("[{}] Throw Exception during verify authentication. {}", this.getClass(), e.getMessage());
            return new ValidationResult(false, messageSource.getMessage("error.auth.invalidToken"));
        }
    }

    private Date nowDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}