package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.StringConvertCase;
import com.lims.api.audit.service.AuditTrailConfigurer;
import com.lims.api.audit.service.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FieldNameConverter implements StringConverter {

    private Map<StringConvertCase, Function<String, String>> converter = new HashMap<>();
    private final AuditTrailConfigurer configurer;

    public FieldNameConverter(AuditTrailConfigurer configurer) {
        this.configurer = configurer;
        this.converter = Map.of(
                StringConvertCase.CAMEL_TO_SNAKE, this::camelToSnake,
                StringConvertCase.SNAKE_TO_CAMEL, this::snakeToCamel
        );
    }

    @Override
    public String convert(String s) {
        return this.converter.get(configurer.convertCase()).apply(s);
    }

    private String camelToSnake(String str) {
        String ret = str.replaceAll("([A-Z])", "_$1").replaceAll("([a-z][A-Z])", "$1_$2");
        return ret.toLowerCase();
    }

    private String snakeToCamel(String str) {
        String[] fragments = str.toLowerCase().split("_");
        return fragments[0] + Arrays.stream(Arrays.copyOfRange(fragments, 1, fragments.length))
                .map(s -> StringUtils.isEmpty(s) ? s : s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining());
    }
}