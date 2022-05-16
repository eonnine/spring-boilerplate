package com.lims.api.filter;

import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.service.impl.AuthJWTProvider;
import com.lims.api.i18n.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCheckFilter implements Filter {

    private static final String[] ALLOW_LIST = {"/auth/**", "/error", "/error/**", "/test"};

    private final AuthProperties authProperties;
    private final AuthJWTProvider authJWTProvider;
    private final LocaleMessageSource messageSource;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String requestURI = request.getRequestURI();

            if (isCheckURI(requestURI)) {
                String accessToken = getAccessToken(request);

                 if (accessToken == null) {
                    sendAuthError(response, "error.auth.notFoundAuthorization");
                    return;
                 }

                String tokenType = accessToken.split(" ")[0];
                if (!authProperties.type.equals(tokenType)) {
                    sendAuthError(response, "error.auth.noSuchTokenType");
                    return;
                }

                boolean isVerified = authJWTProvider.verify(accessToken);

                if (!isVerified) {
                    sendAuthError(response, "error.auth.invalidToken");
                    return;
                }
            }

            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.info("[{}] Unauthenticated request. {}", this.getClass(), e.getMessage());
            throw e;
        }
    }

    private boolean isCheckURI(String uri) {
        return !PatternMatchUtils.simpleMatch(ALLOW_LIST, uri);
    }

    private String getAccessToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> authProperties.jwt.accessToken.cookie.name.equals(cookie.getName()))
                .findAny()
                .orElseGet(() -> new Cookie("empty", null))
                .getValue();
    }

    private void sendAuthError(HttpServletResponse response, String messageCode) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), messageSource.getMessage(messageCode));
    }
}