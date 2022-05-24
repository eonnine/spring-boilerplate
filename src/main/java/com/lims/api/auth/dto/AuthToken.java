package com.lims.api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthToken {
    private final String accessToken;
    private final String refreshToken;

    @Override
    public String toString() {
        return "AuthToken{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}