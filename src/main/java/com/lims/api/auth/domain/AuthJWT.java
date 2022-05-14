package com.lims.api.auth.domain;

import lombok.Builder;

import java.util.Map;

public class AuthJWT extends AuthToken {

    @Builder
    public AuthJWT(String accessToken, String refreshToken) {
        super(accessToken, refreshToken);
    }

}