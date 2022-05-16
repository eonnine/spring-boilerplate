package com.lims.api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthToken {

    private final String accessToken;
    private final String refreshToken;

}