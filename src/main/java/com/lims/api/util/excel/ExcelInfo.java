package com.lims.api.util.excel;

import org.apache.poi.ss.usermodel.CellStyle;

public class ExcelInfo {

    private final Object value;
    private CellStyle cellStyle;

    public ExcelInfo(Object value) {
        this.value = value;
    }

    public ExcelInfo(Object value, CellStyle cellStyle) {
        this.value = value;
        this.cellStyle = cellStyle;
    }

    public Object getValue() {
        return this.value;
    }

    public CellStyle getCellStyle() {
        return cellStyle;
    }

    public boolean isExistsCellStyle() {
        return cellStyle != null;
    }

}