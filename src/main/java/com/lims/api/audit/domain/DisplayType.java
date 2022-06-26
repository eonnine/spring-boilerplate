package com.lims.api.audit.domain;

import org.apache.commons.lang3.StringUtils;

public enum DisplayType {
    COLUMN(DisplayType::getDisplayName),
    COMMENT(DisplayType::getDisplayName);

    private final DisplayTypeFunction<String, String, DisplayType> function;

    DisplayType(DisplayTypeFunction<String, String, DisplayType> function) {
        this.function = function;
    }

    public String displayName(String name, String comment) {
        return function.apply(name, comment, this);
    }

    private static String getDisplayName(String name, String comment, DisplayType type) {
        if (type.isColumn()) {
            return name;
        }
        else if (type.isComment()) {
            return StringUtils.isEmpty(comment) ? name : comment;
        }
        return "";
    }

    private boolean isColumn() {
        return this == DisplayType.COLUMN;
    }

    private boolean isComment() {
        return this == DisplayType.COMMENT;
    }
}