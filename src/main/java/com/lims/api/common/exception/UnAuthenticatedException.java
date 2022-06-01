package com.lims.api.common.exception;

import org.springframework.http.HttpStatus;

public class UnAuthenticatedException extends BaseException {

    public UnAuthenticatedException() {
        this(HttpStatus.UNAUTHORIZED, "error.auth.unauthenticated");
    }

    public UnAuthenticatedException(String messageCode) {
        this(HttpStatus.UNAUTHORIZED, messageCode);
    }

    public UnAuthenticatedException(HttpStatus status, String messageCode) {
        super(status, messageCode);
    }
}