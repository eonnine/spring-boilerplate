package com.lims.api.common.aop;


import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.TokenAuthenticationProvider;
import com.lims.api.common.annotation.LoginCheck;
import com.lims.api.common.exception.UnAuthenticatedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class LoginCheckAspect {

    private final TokenAuthenticationProvider tokenAuthenticationProvider;

    @Before("@annotation(com.lims.api.common.annotation.LoginCheck) && @annotation(loginCheck)")
    public void loginCheck(JoinPoint jp, LoginCheck loginCheck) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        AuthToken authToken = tokenAuthenticationProvider.getAuthentication(request);
        String accessToken = authToken.getAccessToken();

        if (!tokenAuthenticationProvider.verify(accessToken)) {
            log.warn("[{}] Unauthorized Access. token: {}", this.getClass(), accessToken);
            throw new UnAuthenticatedAccessException();
        }
    }

}