package com.lims.api.util.excel;

@FunctionalInterface
public interface ExcelFunction<T, U, R> {

    public R apply(T t, U u);

}