package com.lims.api.auth.controller;

import com.lims.api.auth.domain.AuthToken;
import com.lims.api.auth.model.AuthenticationRequest;
import com.lims.api.auth.model.TokenResponse;
import com.lims.api.auth.service.TokenAuthenticationService;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.annotation.Authed;
import com.lims.api.common.annotation.UseAuthToken;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.model.CommonResponse;
import com.lims.api.common.service.LocaleMessageSource;
import com.lims.api.common.session.AuthTokenSession;
import com.lims.api.config.properties.auth.RefreshTokenProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final RefreshTokenProperties refreshTokenProperties;
    private final LocaleMessageSource messageSource;
    private final TokenAuthenticationService authenticationService;
    private final TokenService tokenService;

    @PostMapping(value = "token")
    public ResponseEntity<TokenResponse> login(@Validated @RequestBody AuthenticationRequest request, HttpSession session) throws UnAuthenticatedException {
        AuthToken authToken = authenticationService.authenticate(request.getUsername(), request.getPassword());

        int timeout = refreshTokenProperties.getExpire().getMaxAge().intValue();
        AuthTokenSession.put(session, authToken.getAccessToken(), authToken.getRefreshToken(), timeout);

        return authenticationService.toResponseEntity(HttpStatus.CREATED, authToken);
    }

    @PostMapping(value = "token/reissue")
    public ResponseEntity<TokenResponse> refreshToken(@UseAuthToken AuthToken token, HttpSession session) throws UnAuthenticatedException {
        boolean existsTokenSession = AuthTokenSession.exists(session, token.getAccessToken());

        if (!existsTokenSession) {
            throw new UnAuthenticatedException("error.auth.revokedToken");
        }

        boolean validTokenSession = !AuthTokenSession.verify(session, token.getAccessToken(), token.getRefreshToken());

        if (!validTokenSession) {
            throw new UnAuthenticatedException("error.auth.invalidToken");
        }

        AuthToken authToken = authenticationService.authenticate(token.getRefreshToken());

        int timeout = refreshTokenProperties.getExpire().getMaxAge().intValue();
        AuthTokenSession.put(session, authToken.getAccessToken(), authToken.getRefreshToken(), timeout);

        return authenticationService.toResponseEntity(HttpStatus.CREATED, authToken);
    }

    @GetMapping(value = "token/verification")
    public ResponseEntity<CommonResponse> verifyToken(@UseAuthToken AuthToken token, HttpSession session) {
        boolean existsTokenSession = AuthTokenSession.exists(session, token.getAccessToken());

        if (!existsTokenSession) {
            String message = messageSource.getMessage("error.auth.revokedToken");
            return ResponseEntity.ok().body(new CommonResponse(false, message));
        }

        boolean validTokenSession = AuthTokenSession.verify(session, token.getAccessToken(), token.getRefreshToken());
        boolean validAccessToken = tokenService.verify(token.getAccessToken());
        
        if (!validTokenSession || !validAccessToken) {
            String message = messageSource.getMessage("error.auth.invalidToken");
            return ResponseEntity.ok().body(new CommonResponse(false, message));
        }

        return ResponseEntity.ok().body(new CommonResponse(true));
    }

    @Authed
    @PostMapping(value = "token/revocation")
    public ResponseEntity<CommonResponse> revoke(@UseAuthToken AuthToken token, HttpSession session) {
        boolean existsTokenSession = AuthTokenSession.exists(session, token.getAccessToken());
        boolean result = false;

        if (existsTokenSession) {
            AuthTokenSession.remove(session, token.getAccessToken());
            result = true;
        }

        return ResponseEntity.ok().body(new CommonResponse(result));
    }

}