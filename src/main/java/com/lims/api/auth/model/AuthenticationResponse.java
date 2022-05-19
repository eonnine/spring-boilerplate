package com.lims.api.auth.model;

import com.lims.api.auth.dto.AuthToken;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;

    public static AuthenticationResponse of(AuthToken authToken) {
        return AuthenticationResponse.builder()
                .accessToken(authToken.getAccessToken())
                .refreshToken(authToken.getRefreshToken())
                .build();
    }
}