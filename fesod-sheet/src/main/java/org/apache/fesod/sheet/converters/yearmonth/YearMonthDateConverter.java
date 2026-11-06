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
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.WorkBookUtil;

/**
 * YearMonth and date converter
 *
 * <p>Excel has no month type, so a month is written as a date cell of the first day of that month, formatted with
 * {@link #DEFAULT_FORMAT}, which keeps sorting and date arithmetic in the sheet working.
 *
 */
public class YearMonthDateConverter implements Converter<YearMonth> {

    /**
     * Default format of a month cell
     */
    public static final String DEFAULT_FORMAT = "yyyy-MM";

    @Override
    public Class<YearMonth> supportJavaTypeKey() {
        return YearMonth.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            YearMonth value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws Exception {
        WriteCellData<?> cellData =
                new WriteCellData<>(value == null ? null : value.atDay(1).atStartOfDay());
        String format = null;
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            format = contentProperty.getDateTimeFormatProperty().getFormat();
        }
        WorkBookUtil.fillDataFormat(cellData, format == null || format.isEmpty() ? null : format, DEFAULT_FORMAT);
        return cellData;
    }
}
