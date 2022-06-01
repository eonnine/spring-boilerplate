package com.lims.api.auth.model;

import com.lims.api.auth.dto.AuthToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse toResponse(AuthToken authToken) {
        return TokenResponse.builder()
                .accessToken(authToken.getAccessToken().get())
                .refreshToken(authToken.getRefreshToken().get())
                .build();
    }
}