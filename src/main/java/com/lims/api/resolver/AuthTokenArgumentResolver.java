package com.lims.api.resolver;

import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.UseAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class AuthTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthProperties authProperties;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UseAuthToken.class);
    }

    @Override
    public AuthToken resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String accessToken = null;
        String refreshToken = null;

        for (Cookie cookie : request.getCookies()) {
            if (authProperties.jwt.accessToken.cookie.name.equals(cookie.getName())) {
                accessToken = cookie.getValue();
            }
            else if (authProperties.jwt.refreshToken.cookie.name.equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}