package com.lims.api.common.aop;


import com.lims.api.auth.domain.Token;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.service.TokenAuthenticationService;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.exception.UnAuthenticatedAccessException;
import com.lims.api.common.session.AuthTokenSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthCheckAspect {

    private final TokenAuthenticationService authenticationService;
    private final TokenService tokenService;

    @Before("@annotation(com.lims.api.common.annotation.Authed)")
    public void AuthCheck(JoinPoint jp) throws UnAuthenticatedAccessException {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        AuthToken authToken = authenticationService.getAuthToken(request);
        Token accessToken = authToken.getAccessToken();
        Token refreshToken = authToken.getRefreshToken();
        boolean validTokenSession = AuthTokenSession.verify(request.getSession(false), accessToken, refreshToken);

        if (!validTokenSession || !tokenService.verify(accessToken)) {
            // TODO change token to user_id in logging
            log.warn("[{}] Unauthorized Access. token: {}", this.getClass(), accessToken);
            throw new UnAuthenticatedAccessException();
        }
    }

}