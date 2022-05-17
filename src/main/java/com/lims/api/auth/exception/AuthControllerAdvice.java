package com.lims.api.auth.exception;

import com.lims.api.exception.domain.UnAuthenticatedException;
import com.lims.api.exception.model.ErrorResponse;
import com.lims.api.i18n.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "com.lims.api.auth.controller")
public class AuthControllerAdvice {

    private final LocaleMessageSource messageSource;

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unAuthenticatedExceptionHandler(UnAuthenticatedException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErrorResponse(messageSource.getMessage(e.getMessageCode())));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> httpClientErrorExceptionHandler(HttpClientErrorException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse(messageSource.getMessage(e.getStatusText())));
    }

}