package com.lims.api.auth.service;

import com.lims.api.auth.domain.Token;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.model.TokenResponse;
import com.lims.api.common.exception.UnAuthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface TokenAuthenticationService {

    AuthToken authenticate(String username, String password) throws UnAuthenticatedException;

    AuthToken authenticate(Token token) throws UnAuthenticatedException;

    AuthToken getAuthToken(HttpServletRequest request);

    ResponseEntity<TokenResponse> toResponseEntity(HttpStatus status, AuthToken authToken);

}