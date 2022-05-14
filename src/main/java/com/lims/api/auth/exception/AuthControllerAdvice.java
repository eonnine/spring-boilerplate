package com.lims.api.auth.exception;

import com.lims.api.exception.domain.UnAuthenticatedException;
import com.lims.api.exception.model.ErrorResponse;
import com.lims.api.i18n.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "com.lims.api.auth.controller")
public class AuthControllerAdvice {

    private final LocaleMessageSource messageSource;

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> test(UnAuthenticatedException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new ErrorResponse(messageSource.getMessage(e.getMessageCode())));
    }

}
