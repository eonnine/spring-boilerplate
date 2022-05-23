package com.lims.api.common.exception;

import org.springframework.http.HttpStatus;

public class UnAuthenticatedException extends BaseException {

    public UnAuthenticatedException() {
        this(HttpStatus.UNAUTHORIZED);
    }

    public UnAuthenticatedException(HttpStatus status) {
        this(status, "error.auth.unauthenticated");
    }

    public UnAuthenticatedException(String messageCode) {
        super(HttpStatus.UNAUTHORIZED, messageCode);
    }

    public UnAuthenticatedException(HttpStatus status, String messageCode) {
        super(status, messageCode);
    }
}