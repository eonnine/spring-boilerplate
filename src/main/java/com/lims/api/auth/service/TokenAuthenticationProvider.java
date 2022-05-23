package com.lims.api.auth.service;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedException;

import javax.servlet.http.HttpServletRequest;

public interface TokenAuthenticationProvider {

    public AuthToken authenticate(String username, String password) throws UnAuthenticatedException;

    public AuthToken renewAuthenticate(String token) throws UnAuthenticatedException;

    public boolean verify(String token);

    public ValidationResult verifyResult(String token);

    public AuthToken getAuthToken(HttpServletRequest request);

}
