package com.lims.api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class AuthToken {

    private final String accessToken;
    private final String refreshToken;
    private final Map<String, Object> claims;

}