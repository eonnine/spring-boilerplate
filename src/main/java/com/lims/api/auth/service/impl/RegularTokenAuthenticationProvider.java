package com.lims.api.auth.service.impl;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.service.TokenAuthenticationProvider;
import com.lims.api.auth.service.TokenProvider;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.properties.auth.AuthProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Log4j2
public abstract class RegularTokenAuthenticationProvider implements TokenAuthenticationProvider {

    private final AuthProperties authProperties;
    private final TokenProvider authTokenProvider;

    public RegularTokenAuthenticationProvider(AuthProperties authProperties, TokenProvider authTokenProvider) {
        this.authProperties = authProperties;
        this.authTokenProvider = authTokenProvider;
    }

    @Override
    public final AuthToken authenticate(String username, String password) throws UnAuthenticatedException {
        try {
            if (username == null || password == null) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "error.auth.notFoundAuthorization");
            }

            // TODO validation using user data
            if (username.equals("ERROR")) {
                throw new UnAuthenticatedException("error.auth.unauthenticated");
            }

            return generateAuthToken();
        } catch (UnAuthenticatedException | HttpClientErrorException e) {
            log.info("[{}] Failed to authenticate user", this.getClass());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public final AuthToken renewAuthenticate(String token) throws UnAuthenticatedException {
        try {
            if (token == null || !verify(token)) {
                throw new UnAuthenticatedException("error.auth.invalidToken");
            }
            return generateAuthToken();
        } catch(UnAuthenticatedException e) {
            log.info("[{}] Failed to refresh issue auth token. {}", this.getClass(), e.getMessage());
            throw e;
        } catch(Exception e) {
            log.error("[{}] Throw Exception during re authentication. {}", this.getClass(), e.getMessage());
            return AuthToken.builder().build();
        }
    }

    @Override
    public final boolean verify(String token) {
        return verifyResult(token).isResult();
    }

    @Override
    public ValidationResult verifyResult(String token) {
        return authTokenProvider.verifyToken(token);
    }

    private AuthToken generateAuthToken() {
//        String accessToken = authTokenProvider.createToken(getExpiresAt(authProperties.getJwt().getAccessToken().getExpire()));
//        String refreshToken = authTokenProvider.createToken(getExpiresAt(authProperties.getJwt().getRefreshToken().getExpire()));

        return AuthToken.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public final AuthToken getAuthToken(HttpServletRequest request) {
        return AuthToken.builder()
                .accessToken(getAccessToken(request))
                .refreshToken(getRefreshToken(request))
                .build();
    }

    private Date dateOf(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date getExpiresAt() {
//        return dateOf(LocalDateTime.now()
//                .plusDays(expire.getDays())
//                .plusHours(expire.getHours())
//                .plusMinutes(expire.getMinutes())
//                .plusSeconds(expire.getSeconds()));
        return new Date();
    }

    protected abstract String getAccessToken(HttpServletRequest request);

    protected abstract String getRefreshToken(HttpServletRequest request);

}