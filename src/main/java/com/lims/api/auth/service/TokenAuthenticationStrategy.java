package com.lims.api.auth.service;

import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.model.TokenResponse;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

public interface TokenAuthenticationStrategy {

    AuthToken getAuthenticationAt(HttpServletRequest request);

    default HttpHeaders makeResponseHeader(AuthToken authToken) { return null; };

    default TokenResponse makeResponseBody(AuthToken authToken) {
        return null;
    };

}