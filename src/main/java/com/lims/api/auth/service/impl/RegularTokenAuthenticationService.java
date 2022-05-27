package com.lims.api.auth.service.impl;

import com.lims.api.auth.domain.Token;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.TokenAuthenticationService;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.properties.auth.AuthProperties;
import com.lims.api.common.properties.auth.TokenProperties;
import com.lims.api.common.properties.auth.domain.ExpireProperty;
import com.lims.api.common.properties.auth.domain.TokenProperty;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Log4j2
public abstract class RegularTokenAuthenticationService implements TokenAuthenticationService {

    private final TokenProperty accessTokenProperties;
    private final TokenProperty refreshTokenProperties;
    private final TokenService tokenService;

    public RegularTokenAuthenticationService(TokenProperties tokenProperties, TokenService tokenService) {
        this.accessTokenProperties = tokenProperties.getAccessToken();
        this.refreshTokenProperties = tokenProperties.getRefreshToken();
        this.tokenService = tokenService;
    }

    @Override
    public AuthToken authenticate(String username, String password) {
        try {
            if (username == null || password == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "error.auth.notFoundAuthorization");
            }

            // TODO validation using user data
            if (username.equals("ERROR")) {
                throw new UnAuthenticatedException();
            }

            return generateAuthToken();
        } catch (UnAuthenticatedException | HttpClientErrorException e) {
            log.info("[{}] Failed to authenticate user", this.getClass());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[{}] Throw Exception during authentication. {}", this.getClass(), e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthToken authenticate(Token token) {
        try {
            if (token == null || !tokenService.verify(token)) {
                throw new UnAuthenticatedException("error.auth.invalidToken");
            }
            return generateAuthToken();
        } catch(UnAuthenticatedException e) {
            log.info("[{}] Failed to authenticate user", this.getClass());
            throw e;
        } catch(Exception e) {
            e.printStackTrace();
            log.error("[{}] Throw Exception during authentication. {}", this.getClass(), e.getMessage());
            throw e;
        }
    }

    private AuthToken generateAuthToken() {
        Token accessToken = tokenService.createToken(dateOf(accessTokenProperties.getExpire()));
        Token refreshToken = tokenService.createToken(dateOf(refreshTokenProperties.getExpire()));

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    protected final Date dateOf(ExpireProperty expire) {
        return Date.from(
                LocalDateTime.now()
                    .plusDays(expire.getDays())
                    .plusHours(expire.getHours())
                    .plusMinutes(expire.getMinutes())
                    .plusSeconds(expire.getSeconds())
                    .atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    @Override
    public final AuthToken getAuthToken(HttpServletRequest request) {
        return AuthToken.builder()
                .accessToken(getAccessToken(request))
                .refreshToken(getRefreshToken(request))
                .build();
    }

    protected abstract Token getAccessToken(HttpServletRequest request);

    protected abstract Token getRefreshToken(HttpServletRequest request);
}