package com.lims.api.filter;

import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.common.domain.ValidationResult;
import com.lims.api.i18n.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {

    private final String[] allowUrlPatterns = {"/auth/**", "/error", "/error/**"};
    private final AuthTokenProvider authTokenProvider;
    private final LocaleMessageSource messageSource;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            if (isCheckURI(request.getRequestURI())) {
                AuthToken authToken = authTokenProvider.getAuthToken(request);
                ValidationResult validationResult = authTokenProvider.verify(authToken.getAccessToken());

                if (!validationResult.isVerified()) {
                    sendAuthError(response, validationResult.getMessageCode());
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
        return !PatternMatchUtils.simpleMatch(allowUrlPatterns, uri);
    }

    private void sendAuthError(HttpServletResponse response, String messageCode) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), messageSource.getMessage(messageCode));
    }
}