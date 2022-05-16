package com.lims.api.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.UseAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final String[] refreshTokenTargetMethods = { "POST" };
    private final String refreshTokenParamName = "refreshToken";
    private final AuthProperties authProperties;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UseAuthToken.class);
    }

    @Override
    public AuthToken resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return authProperties.strategy.isCookie() ? getAuthTokenAtCookie(request) : getAuthTokenAtHeader(request);
    }

    private AuthToken getAuthTokenAtCookie(HttpServletRequest request) {
        String accessToken = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (authProperties.jwt.accessToken.cookie.name.equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
                else if (authProperties.jwt.refreshToken.cookie.name.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private AuthToken getAuthTokenAtHeader(HttpServletRequest request) throws IOException {
        String refreshToken = null;

        if (!PatternMatchUtils.simpleMatch(refreshTokenTargetMethods, request.getMethod().toUpperCase())) {
            refreshToken = getRefreshTokenAtBody(request);
        }
//        if ("POST".equalsIgnoreCase(request.getMethod())) {
//        }

        return AuthToken.builder()
                .accessToken(request.getHeader(authProperties.authHeaderName))
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