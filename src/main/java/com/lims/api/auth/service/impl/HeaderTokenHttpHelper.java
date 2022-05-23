package com.lims.api.auth.service.impl;

import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.model.TokenResponse;
import com.lims.api.auth.service.TokenHttpHelper;
import com.lims.api.common.properties.AuthProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Log4j2
public class HeaderTokenHttpHelper implements TokenHttpHelper {

    private AuthProperties authProperties;

    private final String[] targetMethods = { "POST" };
    private final String paramName = "refreshToken";

    public HeaderTokenHttpHelper(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public ResponseEntity toTokenResponseEntity(HttpStatus status, AuthToken authToken) {
        return ResponseEntity.status(status).body(new TokenResponse().toResponse(authToken));
    }

}
