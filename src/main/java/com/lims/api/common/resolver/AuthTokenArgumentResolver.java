package com.lims.api.common.resolver;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.TokenAuthenticationProvider;
import com.lims.api.common.annotation.UseAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class AuthTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenAuthenticationProvider tokenAuthenticationProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UseAuthToken.class);
    }

    @Override
    public AuthToken resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return tokenAuthenticationProvider.getAuthToken(webRequest.getNativeRequest(HttpServletRequest.class));
    }
}