package com.lims.api.common.util.xss;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

public class XssJackMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Object obj = super.read(type, contextClass, inputMessage);
        return obj;
    }
}