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

package org.apache.fesod.sheet.converters.yearmonth;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

/**
 * YearMonth and string converter
 *
 */
public class YearMonthStringConverter implements Converter<YearMonth> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return YearMonth.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public YearMonth convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return YearMonth.parse(cellData.getStringValue(), formatter(contentProperty, globalConfiguration));
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            YearMonth value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value.format(formatter(contentProperty, globalConfiguration)));
    }

    /**
     * The formatter of the field, {@link YearMonthDateConverter#DEFAULT_FORMAT} when no format is set.
     *
     * <p>{@code @DateTimeFormat} defaults to an empty value and an empty pattern is not a usable one, so an empty
     * format is handled like a missing one. The locale of the read or write is used, so a month name in the
     * configured language can be parsed and written.
     *
     * @param contentProperty the property of the field
     * @param globalConfiguration the configuration of the read or write, which carries the locale
     * @return the formatter to use
     */
    private static DateTimeFormatter formatter(
            ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String format = null;
        if (contentProperty != null
                && contentProperty.getDateTimeFormatProperty() != null
                && StringUtils.isNotBlank(
                        contentProperty.getDateTimeFormatProperty().getFormat())) {
            format = contentProperty.getDateTimeFormatProperty().getFormat();
        }
        if (format == null) {
            format = YearMonthDateConverter.DEFAULT_FORMAT;
        }
        Locale locale = globalConfiguration == null ? null : globalConfiguration.getLocale();
        if (locale == null) {
            return DateTimeFormatter.ofPattern(format);
        }
        return DateTimeFormatter.ofPattern(format, locale);
    }
}
