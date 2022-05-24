package com.lims.api.common.util.xss;

import com.nhncorp.lucy.security.xss.XssPreventer;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XssJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return escape(super.read(type, contextClass, inputMessage));
    }

    private Object escape(Object value) {
        if (isMap(value)) {
            value = ((LinkedHashMap<String, Object>) value).entrySet().stream()
                    .map(o -> {
                        o.setValue(escape(o.getValue()));
                        return o;
                    })
                    .collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue()));
        }
        else if (isArray(value)) {
            value = ((ArrayList<Object>) value).stream().map(o -> escape(o));
        }
        else if (isString(value)) {
            return XssPreventer.escape(String.valueOf(value));
        }
        return value;
    }

    private boolean isArray(Object value) {
        return value instanceof List;
    }

    private boolean isMap(Object value) {
        return value instanceof Map;
    }

    private boolean isString(Object value) {
        return value instanceof String;
    }
}