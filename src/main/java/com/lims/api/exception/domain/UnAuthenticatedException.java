package com.lims.api.exception.domain;

import org.springframework.http.HttpStatus;

public class UnAuthenticatedException extends BaseException {

    public UnAuthenticatedException(String messageCode) {
        super(HttpStatus.UNAUTHORIZED, messageCode);
    }
}
