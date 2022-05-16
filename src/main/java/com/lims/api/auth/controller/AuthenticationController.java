package com.lims.api.auth.controller;

import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.domain.UseAuthToken;
import com.lims.api.auth.model.AuthenticationRequest;
import com.lims.api.auth.model.AuthenticationResponse;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.exception.domain.UnAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthTokenProvider authTokenProvider;
    private final AuthProperties authProperties;

    @GetMapping(value = "login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws UnAuthenticatedException {
        AuthToken authToken = authTokenProvider.generate("123", "123");
        return sendAuthenticationResponse(authToken);
    }

    @PostMapping(value = "token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@UseAuthToken AuthToken token) throws UnAuthenticatedException {
        AuthToken authToken = authTokenProvider.refresh(token.getRefreshToken());
        return sendAuthenticationResponse(authToken);
    }

    @GetMapping(value = "verification")
    public ResponseEntity<Boolean> verifyToken(@UseAuthToken AuthToken token) {
        return ResponseEntity.ok().body(authTokenProvider.verify(token.getAccessToken()));
    }

    private ResponseEntity<AuthenticationResponse> sendAuthenticationResponse(AuthToken authToken) {
        if (authProperties.strategy.isCookie()) {
            HttpHeaders headers =  makeCookieHeaders(authToken);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .headers(headers)
                    .body(null);
        }
        else if(authProperties.strategy.isHeader()) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(AuthenticationResponse.of(authToken));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(null);
    }

    private HttpHeaders makeCookieHeaders(AuthToken authToken) {
        HttpHeaders httpHeaders = new HttpHeaders();

        ResponseCookie accessTokenCookie = ResponseCookie
                .from(authProperties.jwt.accessToken.cookie.name, authToken.getAccessToken())
                .maxAge(authProperties.jwt.accessToken.cookie.maxAge)
                .secure(authProperties.jwt.secure)
                .sameSite(authProperties.jwt.sameSite)
                .httpOnly(authProperties.jwt.httpOnly)
                .path("/")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(authProperties.jwt.refreshToken.cookie.name, authToken.getRefreshToken())
                .maxAge(authProperties.jwt.refreshToken.cookie.maxAge)
                .secure(authProperties.jwt.secure)
                .sameSite(authProperties.jwt.sameSite)
                .httpOnly(authProperties.jwt.httpOnly)
                .path("/")
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return httpHeaders;
    }

}