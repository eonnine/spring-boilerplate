package com.lims.api.audit.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditTrail {
    private AuditType type;
    private String label = "";
    private String content = "";
    private List<SqlRow> originRows;
    private List<SqlRow> updatedRows;

    public AuditTrail() {}

    public String toDiffString() {
        int limit = originRows.size();
        List<String> result = new ArrayList<>();
        for (int i=0; i < limit; i++) {
            SqlRow originRow = originRows.get(i);
            SqlRow updatedRow = updatedRows.get(i);
            String rowString = originRow.entrySet().stream()
                    .filter(origin -> {
                        String key = origin.getKey();
                        return updatedRow.containsKey(key) && !updatedRow.get(key).equals(origin.getValue());
                    })
                    .map(origin -> "{ " + origin.getKey() + ": [" + origin.getValue() + "] -> [" + updatedRow.get(origin.getKey()) + "] }")
                    .collect(Collectors.joining(", "));

            result.add(rowString);
        }
        return toLabelString() + toContentString() + String.join(" / ", result);
    }

    private String toLabelString() {
        return StringUtils.isEmpty(label) ? "" : "[" + label + "]";
    }

    private String toContentString() {
        return StringUtils.isEmpty(content) ? "" :  " " + content + " ";
    }

    public AuditType getType() {
        return type;
    }

    public void setType(AuditType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<SqlRow> getOriginRows() {
        return originRows;
    }

    public void setOriginRows(List<SqlRow> originRows) {
        this.originRows = originRows;
    }

    public List<SqlRow> getUpdatedRows() {
        return updatedRows;
    }

    public void setUpdatedRows(List<SqlRow> updatedRows) {
        this.updatedRows = updatedRows;
    }
}