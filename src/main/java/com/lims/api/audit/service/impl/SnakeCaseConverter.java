package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.ColumnNameConverter;
import com.lims.api.audit.service.StringCaseConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SnakeCaseConverter implements StringCaseConverter {

    private final Map<ColumnNameConverter, Function<String, String>> cases = new HashMap<>();

    public SnakeCaseConverter() {
        cases.put(ColumnNameConverter.CAMEL, this::toCamel);
    }

    @Override
    public String convert(ColumnNameConverter stringCase, String str) {
        return cases.get(stringCase).apply(str);
    }

    private String toCamel(String str) {
        String[] fragments = str.toLowerCase().split("_");
        return fragments[0] + Arrays.stream(Arrays.copyOfRange(fragments, 1, fragments.length))
                .map(s -> StringUtils.isEmpty(s) ? s : s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining());
    }
}