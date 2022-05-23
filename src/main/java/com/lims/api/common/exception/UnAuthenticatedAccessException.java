package com.lims.api.common.exception;

import org.springframework.http.HttpStatus;

public class UnAuthenticatedAccessException extends BaseException {

    public UnAuthenticatedAccessException() {
        this(HttpStatus.FORBIDDEN);
    }

    public UnAuthenticatedAccessException(HttpStatus status) {
        this(status, "error.auth.unauthorizedAccess");
    }

    public UnAuthenticatedAccessException(String messageCode) {
        this(HttpStatus.FORBIDDEN, messageCode);
    }

    public UnAuthenticatedAccessException(HttpStatus status, String messageCode) {
        super(status, messageCode);
    }
}