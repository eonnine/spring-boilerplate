package com.lims.api.auth.service;

import com.lims.api.auth.domain.Token;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.exception.UnAuthenticatedException;

import javax.servlet.http.HttpServletRequest;

public interface TokenAuthenticationService {

    AuthToken authenticate(String username, String password) throws UnAuthenticatedException;

    AuthToken authenticate(Token token) throws UnAuthenticatedException;

    AuthToken getAuthToken(HttpServletRequest request);

}