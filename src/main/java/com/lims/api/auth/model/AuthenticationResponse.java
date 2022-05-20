package com.lims.api.auth.model;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.common.properties.AuthProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;

    public ResponseEntity toResponseEntity(AuthProperties authProperties, AuthToken authToken) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (authProperties.getStrategy().isCookie()) {
            ResponseCookie accessTokenCookie = makeResponseCookie(authProperties, authProperties.getJwt().getAccessToken(), authToken.getAccessToken());
            ResponseCookie refreshTokenCookie = makeResponseCookie(authProperties, authProperties.getJwt().getRefreshToken(), authToken.getRefreshToken());

            httpHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            httpHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .headers(httpHeaders)
                    .body(null);
        }
        else if (authProperties.getStrategy().isHeader()) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(toResponse(authToken));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(null);
    }

    public AuthenticationResponse toResponse(AuthToken authToken) {
        return AuthenticationResponse.builder()
                .accessToken(authToken.getAccessToken())
                .refreshToken(authToken.getRefreshToken())
                .build();
    }

    public ResponseCookie makeResponseCookie(AuthProperties authProperties, AuthProperties.AuthTokenProperty authTokenProperty, String token) {
        return ResponseCookie
                .from(authTokenProperty.getCookie().getName(), token)
                .maxAge(authTokenProperty.getCookie().getMaxAge())
                .secure(authProperties.getJwt().isSecure())
                .sameSite(authProperties.getJwt().getSameSite())
                .httpOnly(authProperties.getJwt().isHttpOnly())
                .path(authProperties.getJwt().getPath())
                .build();
    }
}