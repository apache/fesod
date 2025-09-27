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

package org.apache.fesod.excel.converters.localdatetime;

import java.text.ParseException;
import java.time.LocalDateTime;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.util.DateUtils;

/**
 * LocalDateTime and string converter
 *
 *
 */
public class LocalDateTimeStringConverter implements Converter<LocalDateTime> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDateTime convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws ParseException {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return DateUtils.parseLocalDateTime(cellData.getStringValue(), null, globalConfiguration.getLocale());
        } else {
            return DateUtils.parseLocalDateTime(
                    cellData.getStringValue(),
                    contentProperty.getDateTimeFormatProperty().getFormat(),
                    globalConfiguration.getLocale());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            LocalDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new WriteCellData<>(DateUtils.format(value, null, globalConfiguration.getLocale()));
        } else {
            return new WriteCellData<>(DateUtils.format(
                    value, contentProperty.getDateTimeFormatProperty().getFormat(), globalConfiguration.getLocale()));
        }
    }
}
