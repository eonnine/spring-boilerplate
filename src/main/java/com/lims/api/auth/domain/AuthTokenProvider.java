package com.lims.api.auth.domain;

public interface AuthTokenProvider {

    public AuthToken generate(String username, String password);

    public boolean verify(String token);

    public AuthToken refresh(AuthToken authToken);

}