package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.properties.AuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HeaderAuthTokenProvider extends AbstractAuthTokenProvider {

    private final String[] refreshTokenTargetMethods = { "POST" };
    private final String refreshTokenParamName = "refreshToken";
    private final AuthProperties authProperties;

    public HeaderAuthTokenProvider(AuthProperties authProperties) {
        super(authProperties);
        this.authProperties = authProperties;
    }

    @Override
    public String generateAccessToken() {
        Date tokenExpiresAt = getExpiresAt(authProperties.getJwt().getAccessToken().getExpire());
        return authProperties.getType() + " " + this.createToken(tokenExpiresAt);
    }

    @Override
    public String generateRefreshToken() {
        Date tokenExpiresAt = getExpiresAt(authProperties.getJwt().getAccessToken().getExpire());
        return authProperties.getType() + " " + this.createToken(tokenExpiresAt);
    }

    @Override
    public ValidationResult verifyResult(String token) {
        try {
            if (Strings.isEmpty(token)) {
                return new ValidationResult(false, "error.auth.notFoundAuthorization");
            }

            String[] bearerTokens = token.split(" ");
            String tokenType = bearerTokens[0];
            String jwt = bearerTokens[1];

            if (!authProperties.getType().equals(tokenType)) {
                return new ValidationResult(false, "error.auth.notDefineTokenType");
            }

            JWTVerifier verifier = createJWTVerifier(authProperties);
            verifier.verify(jwt);

            return new ValidationResult(true);

        } catch (JWTVerificationException e) {
            log.info("[{}] Failed to verify auth token. {}", this.getClass(), e.getMessage());
            return new ValidationResult(false, "error.auth.invalidToken");

        } catch (Exception e){
            e.printStackTrace();
            return new ValidationResult(false, "error.auth.default");
        }    }

    @Override
    public AuthToken getAuthToken(HttpServletRequest request) {
        if (request == null) {
            return AuthToken.builder().build();
        }

        String accessToken = request.getHeader(authProperties.getAuthHeaderName());
        String refreshToken = null;

        if (PatternMatchUtils.simpleMatch(refreshTokenTargetMethods, request.getMethod().toUpperCase())) {
            try {
                refreshToken = getRefreshTokenAtBody(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String getRefreshTokenAtBody(HttpServletRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String bodyString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, String> body = mapper.readValue(bodyString, Map.class);
        return body.get(refreshTokenParamName);
    }
}