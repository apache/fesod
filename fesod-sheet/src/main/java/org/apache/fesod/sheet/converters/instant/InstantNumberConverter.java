/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fesod.sheet.converters.instant;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Instant and number converter
 *
 * <p>Excel stores a date as a number without a time zone, so the value is read and written with the default time
 * zone of the JVM, the same way {@link java.util.Date} is handled.
 */
public class InstantNumberConverter implements Converter<Instant> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Instant.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public Instant convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        LocalDateTime localDateTime = DateUtils.getLocalDateTime(
                cellData.getNumberValue().doubleValue(), DateUtils.isDate1904(contentProperty, globalConfiguration));
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            Instant value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(value, ZoneId.systemDefault());
        return new WriteCellData<>(BigDecimal.valueOf(
                DateUtil.getExcelDate(localDateTime, DateUtils.isDate1904(contentProperty, globalConfiguration))));
    }
}
