/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.apache.fesod.sheet.converters.sqltime;

import java.math.BigDecimal;
import java.sql.Time;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

/** java.sql.Time and number converter. */
public class SqlTimeNumberConverter implements Converter<Time> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Time.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public Time convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return Time.valueOf(DateUtils.getLocalTime(
                cellData.getNumberValue().doubleValue(), DateUtils.isDate1904(contentProperty, globalConfiguration)));
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Time value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(BigDecimal.valueOf(DateUtil.getExcelDate(
                value.toLocalTime().atDate(DateUtils.EPOCH),
                DateUtils.isDate1904(contentProperty, globalConfiguration))));
    }
}
