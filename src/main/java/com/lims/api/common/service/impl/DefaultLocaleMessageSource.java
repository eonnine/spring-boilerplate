package com.lims.api.common.service.impl;

import com.lims.api.common.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultLocaleMessageSource implements LocaleMessageSource {

    private final MessageSource messageSource;

    @Override
    public String getMessage(String messageCode) {
        try {
            return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
        } catch(Exception e) {
            log.error("[{}] {}", this.getClass(), e.getMessage());
            return null;
        }
    }

    @Override
    public String getMessage(String messageCode, Object[] args) {
        try {
            return messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
        } catch(Exception e) {
            log.error("[{}] {}", this.getClass(), e.getMessage());
            return null;
        }
    }
}