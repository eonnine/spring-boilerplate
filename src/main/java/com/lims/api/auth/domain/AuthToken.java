package com.lims.api.auth.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthToken {
    private final Token accessToken;
    private final Token refreshToken;
}