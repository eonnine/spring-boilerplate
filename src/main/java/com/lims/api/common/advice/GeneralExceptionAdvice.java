package com.lims.api.common.advice;

import com.lims.api.common.exception.UnAuthenticatedException;
import com.lims.api.common.model.ErrorResponse;
import com.lims.api.common.i18n.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GeneralExceptionAdvice {

    private final LocaleMessageSource messageSource;

    public GeneralExceptionAdvice(LocaleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unAuthenticatedExceptionHandler(UnAuthenticatedException e) {
        return new ResponseEntity<>(new ErrorResponse(messageSource.getMessage(e.getMessageCode())), e.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> httpClientErrorExceptionHandler(HttpClientErrorException e) {
        return new ResponseEntity<>(new ErrorResponse(messageSource.getMessage(e.getStatusText())), e.getStatusCode());
    }

}