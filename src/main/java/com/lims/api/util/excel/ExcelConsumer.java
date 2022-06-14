package com.lims.api.util.excel;

@FunctionalInterface
public interface ExcelConsumer<T, U> {

    public void apply(T t, U u);

}