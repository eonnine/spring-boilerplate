package com.lims.api.common.aop;


import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.common.annotation.LoginCheck;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedAccessException;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoginCheckAspect {

    private AuthTokenProvider authTokenProvider;

    public LoginCheckAspect(AuthTokenProvider authTokenProvider) {
        this.authTokenProvider = authTokenProvider;
    }

    @Before("@annotation(com.lims.api.common.annotation.LoginCheck) && @annotation(loginCheck)")
    public void loginCheck(JoinPoint jp, LoginCheck loginCheck) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        AuthToken authToken = authTokenProvider.getAuthToken(request);
        String accessToken = authToken.getAccessToken();

        if (!authTokenProvider.verify(accessToken)) {
            throw new UnAuthenticatedAccessException();
        }
    }

}