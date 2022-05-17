package com.lims.api.auth.service;

import com.lims.api.auth.domain.AuthToken;
import com.lims.api.common.domain.ValidationResult;

import javax.servlet.http.HttpServletRequest;

public interface AuthTokenProvider {

    public AuthToken generate(String username, String password);

    public AuthToken refresh(String refreshToken);

    public ValidationResult verify(String token);

    public AuthToken getAuthToken(HttpServletRequest request);

}