package com.lims.api.auth.domain;

public interface AuthTokenProvider {

    public AuthToken generate(String username, String password);

    public boolean verify(AuthToken token);

    public AuthToken refresh(AuthToken token);

}