package com.lims.api.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.api.auth.domain.HeaderStrategyCondition;
import com.lims.api.auth.domain.Token;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.properties.auth.AccessTokenProperties;
import com.lims.api.common.properties.auth.RefreshTokenProperties;
import com.lims.api.common.properties.auth.TokenProperties;
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
public class HeaderTokenAuthenticationService extends RegularTokenAuthenticationService {

    private final String authHeaderName = "authorization";
    private final String[] targetMethods = { "POST" };
    private final String paramName = "refreshToken";

    public HeaderTokenAuthenticationService(TokenProperties tokenProperties, TokenService tokenService) {
        super(tokenProperties, tokenService);
    }

    @Override
    protected Token getAccessToken(HttpServletRequest request) {
        return new Token(request.getHeader(authHeaderName));
    }

    @Override
    protected Token getRefreshToken(HttpServletRequest request) {
        if (PatternMatchUtils.simpleMatch(targetMethods, request.getMethod().toUpperCase())) {
            return new Token(getRefreshTokenAtBody(request));
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
