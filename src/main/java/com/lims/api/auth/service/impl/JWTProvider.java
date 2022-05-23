package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lims.api.auth.service.TokenProvider;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.i18n.service.LocaleMessageSource;
import com.lims.api.common.properties.AuthProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Log4j2
@Service
public class JWTProvider implements TokenProvider {

    private final LocaleMessageSource messageSource;
    private AuthProperties authProperties;
    private Algorithm algorithm;
    private JWTVerifier verifier;
    private final Map<String, Object> header = Map.of(
            "alg", "HS256",
            "typ", "JWT"
    );

    public JWTProvider(AuthProperties authProperties, LocaleMessageSource messageSource) {
        this.messageSource = messageSource;
        this.authProperties = authProperties;

        Algorithm algorithm = Algorithm.HMAC256(authProperties.getJwt().getSecret());
        this.algorithm = algorithm;
        this.verifier = JWT.require(algorithm).withIssuer(authProperties.getJwt().getIssuer()).build();
    }

    @Override
    public String createToken(Date expiresAt) {
        return tokenPrefix() +
                JWT.create()
                        .withHeader(header)
                        .withIssuer(authProperties.getJwt().getIssuer())
                        .withNotBefore(nowDate())
                        .withIssuedAt(nowDate())
                        .withExpiresAt(expiresAt)
                        .sign(algorithm);
    }

    @Override
    public ValidationResult verifyToken(String token) {
        try {
            if (Strings.isEmpty(token)) {
                throw new UnAuthenticatedException("error.auth.notFoundAuthorization");
            }
            verifier.verify(token);
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

    private String tokenPrefix() {
        return Strings.isEmpty(authProperties.getType()) ? "" : authProperties.getType() + " ";
    }

    private Date nowDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

}
