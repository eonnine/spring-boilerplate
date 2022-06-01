package com.lims.api.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.api.auth.domain.HeaderStrategyCondition;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.model.TokenResponse;
import com.lims.api.auth.service.TokenStrategy;
import com.lims.api.common.properties.auth.RefreshTokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@Conditional(HeaderStrategyCondition.class)
@RequiredArgsConstructor
public class HeaderTokenStrategy implements TokenStrategy {

    private final String[] targetMethods = { "POST" };
    private final RefreshTokenProperties refreshTokenProperties;

    @Override
    public Token findAccessToken(HttpServletRequest request) {
        return new Token(request.getHeader("authorization"));
    }

    @Override
    public Token findRefreshToken(HttpServletRequest request) {
        if (PatternMatchUtils.simpleMatch(targetMethods, request.getMethod().toUpperCase())) {
            return new Token(getRefreshTokenAtBody(request, refreshTokenProperties.getName()));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getRefreshTokenAtBody(HttpServletRequest request, String paramName) {
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

    @Override
    public TokenResponse makeResponseBody(AuthToken authToken) {
        return new TokenResponse().toResponse(authToken);
    }

}
