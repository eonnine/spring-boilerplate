package com.lims.api.auth.service;

import com.lims.api.auth.domain.AuthToken;
import com.lims.api.exception.domain.UnAuthenticatedException;

public interface AuthTokenProvider {

    public AuthToken generate(String username, String password) throws UnAuthenticatedException;

    public boolean verify(String token);

    public AuthToken refresh(String refreshToken) throws UnAuthenticatedException;

}