package com.lims.api.common.util.excel;

public class ExcelInfoRowWithIndex extends ExcelInfoRow {

    private int rowIndex;

    public ExcelInfoRowWithIndex(int rowIndex, ExcelInfo... columns) {
        super(columns);
        this.rowIndex = rowIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

}