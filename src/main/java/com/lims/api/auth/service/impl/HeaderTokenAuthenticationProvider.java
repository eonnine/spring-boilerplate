package com.lims.api.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.api.auth.service.TokenProvider;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.properties.AuthProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class HeaderTokenAuthenticationProvider extends RegularTokenAuthenticationProvider {

    private AuthProperties authProperties;

    private final String[] targetMethods = { "POST" };
    private final String paramName = "refreshToken";

    public HeaderTokenAuthenticationProvider(AuthProperties authProperties, TokenProvider authTokenProvider) {
        super(authProperties, authTokenProvider);
        this.authProperties = authProperties;
    }

    @Override
    public ValidationResult verifyResult(String bearerToken) {
        String token = Optional.ofNullable(bearerToken)
                .map(s ->  s.split(" "))
                .filter(s -> s.length == 2 && authProperties.getType().equals(s[0]))
                .orElseGet(() -> new String[2])
                [1];
        return super.verifyResult(token);
    }

    @Override
    protected String getAccessToken(HttpServletRequest request) {
        return request.getHeader(authProperties.getAuthHeaderName());
    }

    @Override
    protected String getRefreshToken(HttpServletRequest request) {
        if (PatternMatchUtils.simpleMatch(targetMethods, request.getMethod().toUpperCase())) {
            return getRefreshTokenAtBody(request);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getRefreshTokenAtBody(HttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String bodyString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Map<String, String> body = mapper.readValue(bodyString, Map.class);
            return body.get(paramName);
        } catch (IOException e) {
            log.error("[{}] Throw IOException during get parameter '{}' from HttpServletRequest. {}", this.getClass(), paramName, e.getMessage());
            return null;
        }
    }
}
