package com.lims.api.audit.domain;

public enum DisplayType {
    COLUMN,
    COMMENT;

    public boolean isColumn() {
        return this == DisplayType.COLUMN;
    }

    public boolean isComment() {
        return this == DisplayType.COMMENT;
    }
}