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

    private final String[] allowUrlPatterns = {"/auth/**", "/error", "/error/**"};

    private final AuthProperties authProperties;
    private final AuthJWTProvider authJWTProvider;
    private final LocaleMessageSource messageSource;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            if (isCheckURI(request.getRequestURI())) {
                if (authProperties.strategy.isCookie() && !isValidCookieBasedAuth(request, response)) {
                    return;
                }
                else if (authProperties.strategy.isHeader() && !isValidHeaderBasedAuth(request, response)) {
                    return;
                }
            }

            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.info("[{}] Unauthenticated request. {}", this.getClass(), e.getMessage());
            throw e;
        }
    }

    private boolean isValidCookieBasedAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = getAccessTokenAtCookie(request);
        if (accessToken == null) {
            sendAuthError(response, "error.auth.notFoundAuthorization");
            return false;
        }

        boolean isVerified = authJWTProvider.verify(accessToken);
        if (!isVerified) {
            sendAuthError(response, "error.auth.invalidToken");
            return false;
        }
        return true;
    }

    private boolean isValidHeaderBasedAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bearerAccessToken = request.getHeader(authProperties.authHeaderName);
        if (bearerAccessToken == null) {
            sendAuthError(response, "error.auth.notFoundAuthorization");
            return false;
        }

        String tokenType = bearerAccessToken.split(" ")[0];
        if (!authProperties.type.equals(tokenType)) {
            sendAuthError(response, "error.auth.notDefineTokenType");
            return false;
        }

        boolean isVerified = authJWTProvider.verify(bearerAccessToken);
        if (!isVerified) {
            sendAuthError(response, "error.auth.invalidToken");
            return false;
        }
        return true;
    }

    private boolean isCheckURI(String uri) {
        return !PatternMatchUtils.simpleMatch(allowUrlPatterns, uri);
    }

    private String getAccessTokenAtCookie(HttpServletRequest request) {
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