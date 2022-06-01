package com.lims.api.common.util.excel;

import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.function.Consumer;

public class ExcelInfoRow {

    private final ExcelInfo[] columns;
    private Consumer<XSSFRow> handler;

    public ExcelInfoRow(ExcelInfo... columns) {
        this.columns = columns;
    }

    public ExcelInfo[] getColumns() {
        return this.columns;
    }

    public Consumer<XSSFRow> getHandler() {
        return handler;
    }

    public ExcelInfoRow handle(Consumer<XSSFRow> handler) {
        this.handler = handler;
        return this;
    }

    public boolean isExistsHandler() {
        return this.handler != null;
    }

}