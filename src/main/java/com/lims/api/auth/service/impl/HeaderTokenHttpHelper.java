package com.lims.api.auth.service.impl;

import com.lims.api.auth.domain.HeaderStrategyCondition;
import com.lims.api.auth.dto.AuthToken;
import com.lims.api.auth.model.TokenResponse;
import com.lims.api.auth.service.TokenHttpHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Conditional(HeaderStrategyCondition.class)
public class HeaderTokenHttpHelper implements TokenHttpHelper {

    @Override
    public ResponseEntity toResponseEntity(HttpStatus status, AuthToken authToken) {
        return ResponseEntity.status(status).body(new TokenResponse().toResponse(authToken));
    }

}