package com.lims.api.auth.domain;

public class AuthJWT extends AuthToken {

    public AuthJWT(String accessToken, String refreshToken) {
        super(accessToken, refreshToken);
    }

}