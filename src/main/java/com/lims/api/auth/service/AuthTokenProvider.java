package com.lims.api.auth.service;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedException;

import javax.servlet.http.HttpServletRequest;

public interface AuthTokenProvider {

    public AuthToken generate(String username, String password) throws UnAuthenticatedException;

    public AuthToken refresh(String refreshToken) throws UnAuthenticatedException;

    public boolean verify(String token);

    public ValidationResult verifyResult(String token);

    public AuthToken getAuthToken(HttpServletRequest request);
}