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

import java.sql.Time;
import java.time.LocalDateTime;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.util.WorkBookUtil;

/** java.sql.Time and date converter. */
public class SqlTimeDateConverter implements Converter<Time> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Time.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Time value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        LocalDateTime localDateTime = value == null ? null : value.toLocalTime().atDate(DateUtils.EPOCH);
        WriteCellData<?> cellData = new WriteCellData<>(localDateTime);
        String format = null;
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            format = contentProperty.getDateTimeFormatProperty().getFormat();
        }
        WorkBookUtil.fillDataFormat(
                cellData, format == null || format.isEmpty() ? null : format, DateUtils.DEFAULT_LOCAL_TIME_FORMAT);
        return cellData;
    }
}
