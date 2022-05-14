package com.lims.api.util.i18n.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultLocaleMessageSource implements LocaleMessageSource {

    private final MessageSource messageSource;

    @Override
    public String getMessage(String messageCode) {
        return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
    }

    @Override
    public String getMessage(String messageCode, Object[] args) {
        return messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
    }
}
