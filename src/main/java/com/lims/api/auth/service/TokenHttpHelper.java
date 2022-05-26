package com.lims.api.auth.service;

import com.lims.api.auth.dto.AuthToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface TokenHttpHelper {

    ResponseEntity toResponseEntity(HttpStatus status, AuthToken authToken);

}
