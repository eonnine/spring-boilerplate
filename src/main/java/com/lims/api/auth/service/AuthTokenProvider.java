package com.lims.api.auth.service;

import com.lims.api.auth.domain.AuthToken;

public interface AuthTokenProvider {

    public AuthToken generate(String username, String password);

    public boolean verify(String token);

    public AuthToken refresh(String refreshToken);

}