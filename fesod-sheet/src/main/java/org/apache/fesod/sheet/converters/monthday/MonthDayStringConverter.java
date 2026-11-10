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

package org.apache.fesod.sheet.converters.monthday;

import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.DateUtils;

/**
 * MonthDay and string converter
 *
 *
 */
public class MonthDayStringConverter implements Converter<MonthDay> {

    private static final String LENIENT_FORMAT = "MM-dd";

    @Override
    public Class<?> supportJavaTypeKey() {
        return MonthDay.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public MonthDay convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        String format = customFormat(contentProperty);
        if (format != null) {
            return DateUtils.parseMonthDay(stringValue, format, globalConfiguration.getLocale());
        }
        try {
            return DateUtils.parseMonthDay(stringValue, null, globalConfiguration.getLocale());
        } catch (DateTimeParseException e) {
            return DateUtils.parseMonthDay(stringValue, LENIENT_FORMAT, globalConfiguration.getLocale());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            MonthDay value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(
                DateUtils.format(value, customFormat(contentProperty), globalConfiguration.getLocale()));
    }

    private String customFormat(ExcelContentProperty contentProperty) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return null;
        }
        String format = contentProperty.getDateTimeFormatProperty().getFormat();
        return StringUtils.isEmpty(format) ? null : format;
    }
}
