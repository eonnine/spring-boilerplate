package com.lims.api.util.excel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class ExcelCreator {

    private XSSFSheet sheet = null;
    private int headerBeginIndex = 0;

    private XSSFWorkbook workbook;
    private ExcelInfoRow header;
    private List<ExcelInfoRow> body;
    private List<ExcelInfoRowWithIndex> unorderedData;

    public ExcelCreator() {
        this.createWorkbook();
    }

    public ExcelCreator createSheet(String sheetName) {
        this.sheet = this.workbook.createSheet(sheetName);
        this.setHeaderBeginIndex(0);
        return this;
    }

    public ExcelCreator setHeaderBeginIndex(int headerBeginIndex) {
        this.headerBeginIndex = headerBeginIndex;
        return this;
    }

    public ExcelCreator setHeader(ExcelFunction<XSSFWorkbook, XSSFSheet, ExcelInfoRow> factory) {
        this.header = factory.apply(this.workbook, this.sheet);
        return this;
    }

    public ExcelCreator setBody(ExcelFunction<XSSFWorkbook, XSSFSheet, List<ExcelInfoRow>> factory) {
        this.body = factory.apply(this.workbook, this.sheet);
        return this;
    }

    /**
     * 데이터를 액셀 인스턴스에 저장한 후 진행해야하는 프로세스를 위한 메서드입니다.
     */
    public ExcelCreator postHandle(ExcelConsumer<XSSFWorkbook, XSSFSheet> handler) {
        handler.apply(this.workbook, this.sheet);
        return this;
    }

    /**
     * header, body와 달리 순서에 구애받지 않고 임의로 추가해야 하는 데이터를 위한 메서드입니다.
     *
     * ※ 삽입하는 rowIndex가 header 및 body의 데이터의 rowIndex와 겹치지 않게 주의해주세요.
     */
    public ExcelCreator setUnorderedData(ExcelFunction<XSSFWorkbook, XSSFSheet, List<ExcelInfoRowWithIndex>> factory) {
        this.unorderedData = factory.apply(this.workbook, this.sheet);
        return this;
    }

    public ExcelCreator make() {
        List<ExcelInfoRow> data = this.body;
        XSSFSheet sheet = this.sheet;
        int rowIndex = this.headerBeginIndex;

        makeHeader(sheet, rowIndex);

        for (ExcelInfoRow excelInfoRow : data) {
            rowIndex++;

            XSSFRow row = sheet.createRow(rowIndex);

            if (excelInfoRow.isExistsHandler()) {
                excelInfoRow.getHandler().accept(row);
            }

            makeColumns(row, excelInfoRow.getColumns());
        }

        makeUnorderedData(sheet);

        return this;
    }

    public XSSFWorkbook complete() {
        return this.workbook;
    }

    private void createWorkbook() {
        this.workbook = new XSSFWorkbook();
    }

    private void makeHeader(XSSFSheet sheet, int headerRowIndex) {
        ExcelInfoRow header = this.header;
        XSSFRow row = sheet.createRow(headerRowIndex);

        if (header.isExistsHandler()) {
            header.getHandler().accept(row);
        }

        makeColumns(row, header.getColumns());
    }

    private void makeUnorderedData(XSSFSheet sheet) {
        List<ExcelInfoRowWithIndex> data = this.unorderedData;

        for (ExcelInfoRowWithIndex excelInfoRow : data) {
            int rowindex = excelInfoRow.getRowIndex();
            XSSFRow row = sheet.createRow(rowindex);

            if (excelInfoRow.isExistsHandler()) {
                excelInfoRow.getHandler().accept(row);
            }

            makeColumns(row, excelInfoRow.getColumns());
        }
    }

    private void makeColumns(XSSFRow row, ExcelInfo[] columns) {
        int columnIndex = 0;
        for (ExcelInfo column : columns) {
            XSSFCell cell = row.createCell(columnIndex);

            setValueToCell(cell, column.getValue());

            if (column.isExistsCellStyle()) {
                cell.setCellStyle(column.getCellStyle());
            }

            columnIndex++;
        }
    }

    private void setValueToCell(XSSFCell cell, Object value) {
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

}