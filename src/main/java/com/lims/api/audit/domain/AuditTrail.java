package com.lims.api.audit.domain;

import com.lims.api.audit.config.AuditConfigurer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditTrail {
    private String label = "";
    private String content = "";
    private List<SqlRow> originRows;
    private List<SqlRow> updatedRows;

    public AuditTrail() {}

    public AuditString toAuditString(AuditConfigurer configurer) {
        return AuditString.builder()
                .label(getLabel())
                .content(getContent())
                .diffString(getDiffString(configurer.displayType()))
                .build();
    }

    public boolean isUpdated() {
        boolean isUpdated = false;
        int limit = originRows.size();
        for (int i=0; i < limit; i++) {
            SqlRow originRow = originRows.get(i);
            SqlRow updatedRow = updatedRows.get(i);
            boolean isSame = originRow.entrySet().stream()
                    .allMatch(origin -> {
                        String key = origin.getKey();
                        SqlColumn originColumn = origin.getValue();

                        if (!updatedRow.containsKey(key)) {
                            return false;
                        }
                        SqlColumn updatedColumn = updatedRow.get(key);
                        return equalsValue(updatedColumn.getData(), originColumn.getData());
                    });

            if (!isSame) {
                isUpdated = true;
                break;
            }
        }
        return isUpdated;
    }

    private String getDiffString(DisplayType displayType) {
        List<String> result = new ArrayList<>();

        for (int i=0; i < originRows.size(); i++) {
            SqlRow originRow = originRows.get(i);
            SqlRow updatedRow = updatedRows.get(i);
            String rowString = originRow.entrySet().stream()
                    .filter(origin -> {
                        String key = origin.getKey();
                        SqlColumn originColumn = origin.getValue();

                        if (!updatedRow.containsKey(key)) {
                            return false;
                        }
                        SqlColumn updatedColumn = updatedRow.get(key);
                        return notEqualsValue(updatedColumn.getData(), originColumn.getData());
                    })
                    .map(origin -> "{ "
                            + getColumnLabel(displayType, origin.getValue()) + ": "
                            + "[" + origin.getValue().getData() + "] -> "
                            + "[" + updatedRow.get(origin.getKey()).getData() + "]"
                            + " }")
                    .collect(Collectors.joining(", "));

            result.add(rowString);
        }
        return String.join(" / ", result);
    }

    private String getColumnLabel(DisplayType displayType, SqlColumn column) {
        if (displayType.isColumn()) {
            return column.getData();
        }
        else if (displayType.isComment()) {
            return StringUtils.isEmpty(column.getComment()) ? column.getData() : column.getComment();
        }
        return "";
    }

    private boolean equalsValue(String s, String s2) {
        return s.equals(s2);
    }

    private boolean notEqualsValue(String s, String s2) {
        return !s.equals(s2);
    }

    private String getLabel() {
        return label;
    }

    private String getContent() {
        return content;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOriginRows(List<SqlRow> originRows) {
        this.originRows = originRows;
    }

    public void setUpdatedRows(List<SqlRow> updatedRows) {
        this.updatedRows = updatedRows;
    }
}