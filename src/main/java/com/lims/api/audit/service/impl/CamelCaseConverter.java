package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.ColumnNameConverter;
import com.lims.api.audit.service.StringCaseConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CamelCaseConverter implements StringCaseConverter {

    private final Map<ColumnNameConverter, Function<String, String>> cases = new HashMap<>();

    public CamelCaseConverter() {
        cases.put(ColumnNameConverter.SNAKE, this::toSnake);
    }

    @Override
    public String convert(ColumnNameConverter stringCase, String str) {
        return cases.get(stringCase).apply(str);
    }

    private String toSnake(String str) {
        String ret = str.replaceAll("([A-Z])", "_$1").replaceAll("([a-z][A-Z])", "$1_$2");
        return ret.toLowerCase();
    }
}