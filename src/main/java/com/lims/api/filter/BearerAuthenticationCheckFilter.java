package com.lims.api.filter;

import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.service.impl.AuthJWTProvider;
import com.lims.api.i18n.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class BearerAuthenticationCheckFilter implements Filter {

    private static final String[] ALLOW_LIST = {"/auth/**", "/error/**"};

    private final String authHeaderName = "authorization";
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
                String authHeader = request.getHeader(authHeaderName);

                if (authHeader == null) {
                    response.sendError(HttpStatus.FORBIDDEN.value());
                    return;
                }

                String[] bearerAuths = authHeader.split(" ");

                if (!authProperties.type.equals(bearerAuths[0])) {
                    sendAuthError(response, "error.auth.noSuchTokenType");
                    return;
                }

                String accessToken = bearerAuths[1];
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

    private void sendAuthError(HttpServletResponse response, String messageCode) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), messageSource.getMessage(messageCode));
    }
}
