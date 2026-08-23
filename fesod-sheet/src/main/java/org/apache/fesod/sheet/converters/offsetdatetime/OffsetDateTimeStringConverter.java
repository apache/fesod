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

package org.apache.fesod.sheet.converters.offsetdatetime;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.DateUtils;

/** OffsetDateTime and string converter. */
public class OffsetDateTimeStringConverter implements Converter<OffsetDateTime> {
    /**
     * Thread-local cache of {@link DateTimeFormatter} instances, keyed by pattern and locale, so
     * the per-cell hot path does not rebuild formatters for every conversion.
     */
    private static final ThreadLocal<Map<String, DateTimeFormatter>> FORMATTER_CACHE = new ThreadLocal<>();

    @Override
    public Class<?> supportJavaTypeKey() {
        return OffsetDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public OffsetDateTime convertToJavaData(
            ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        String format = format(contentProperty);
        DateTimeFormatter formatter = formatter(contentProperty, globalConfiguration.getLocale());
        try {
            return OffsetDateTime.parse(stringValue, formatter);
        } catch (DateTimeParseException e) {
            try {
                return DateUtils.parseLocalDateTime(stringValue, format, globalConfiguration.getLocale())
                        .atZone(ZoneId.systemDefault())
                        .toOffsetDateTime();
            } catch (RuntimeException inner) {
                if (StringUtils.isEmpty(format)) {
                    return LocalDateTime.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            .atZone(ZoneId.systemDefault())
                            .toOffsetDateTime();
                }
                throw inner;
            }
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            OffsetDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value.format(formatter(contentProperty, globalConfiguration.getLocale())));
    }

    private String format(ExcelContentProperty contentProperty) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return null;
        }
        return contentProperty.getDateTimeFormatProperty().getFormat();
    }

    private DateTimeFormatter formatter(ExcelContentProperty contentProperty, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String format = format(contentProperty);
        if (StringUtils.isEmpty(format)) {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        }
        final String pattern = format;
        final Locale actualLocale = locale;
        Map<String, DateTimeFormatter> cache = FORMATTER_CACHE.get();
        if (cache == null) {
            cache = MapUtils.newHashMap();
            FORMATTER_CACHE.set(cache);
        }
        return cache.computeIfAbsent(
                pattern + '\0' + actualLocale, key -> DateTimeFormatter.ofPattern(pattern, actualLocale));
    }
}
