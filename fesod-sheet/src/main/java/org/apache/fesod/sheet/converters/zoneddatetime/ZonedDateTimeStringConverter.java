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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.converters.zoneddatetime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

/** ZonedDateTime and string converter. */
public class ZonedDateTimeStringConverter implements Converter<ZonedDateTime> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return ZonedDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public ZonedDateTime convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        DateTimeFormatter formatter = formatter(contentProperty, globalConfiguration.getLocale());
        try {
            return ZonedDateTime.parse(cellData.getStringValue(), formatter);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(cellData.getStringValue(), formatter).atZone(ZoneId.systemDefault());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            ZonedDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value.format(formatter(contentProperty, globalConfiguration.getLocale())));
    }

    private DateTimeFormatter formatter(ExcelContentProperty contentProperty, Locale locale) {
        if (contentProperty == null
                || contentProperty.getDateTimeFormatProperty() == null
                || StringUtils.isEmpty(
                        contentProperty.getDateTimeFormatProperty().getFormat())) {
            return DateTimeFormatter.ISO_ZONED_DATE_TIME;
        }
        return DateTimeFormatter.ofPattern(
                contentProperty.getDateTimeFormatProperty().getFormat(), locale);
    }
}
