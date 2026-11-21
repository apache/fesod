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

package org.apache.fesod.sheet.converters.offsetdatetime;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.WriteConverterContext;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.util.WorkBookUtil;

/**
 * OffsetDateTime and date converter.
 *
 * <p>DATE cells store only a local date-time, so XLSX and XLS write the local date-time and discard the offset.
 * For CSV without an explicit {@code @DateTimeFormat}, this converter writes an ISO-8601 string with the offset
 * so the value can be read back as an {@link OffsetDateTime}. A configured {@code @DateTimeFormat} keeps the
 * DATE-cell behavior and does not preserve the offset; use {@link OffsetDateTimeStringConverter} when a custom
 * string representation must carry the offset.
 */
public class OffsetDateTimeDateConverter implements Converter<OffsetDateTime> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return OffsetDateTime.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<OffsetDateTime> context) throws Exception {
        if (isCsv(context) && StringUtils.isEmpty(format(context.getContentProperty()))) {
            return new WriteCellData<>(context.getValue().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
        return convertToExcelData(
                context.getValue(),
                context.getContentProperty(),
                context.getWriteContext().currentWriteHolder().globalConfiguration());
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            OffsetDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws Exception {
        LocalDateTime localDateTime = value.toLocalDateTime();
        WriteCellData<?> cellData = new WriteCellData<>(localDateTime);
        WorkBookUtil.fillDataFormat(cellData, format(contentProperty), DateUtils.defaultDateFormat);
        return cellData;
    }

    private boolean isCsv(WriteConverterContext<OffsetDateTime> context) {
        return context.getWriteContext() != null
                && context.getWriteContext().writeWorkbookHolder() != null
                && ExcelTypeEnum.CSV.equals(
                        context.getWriteContext().writeWorkbookHolder().getExcelType());
    }

    private String format(ExcelContentProperty contentProperty) {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return null;
        }
        String format = contentProperty.getDateTimeFormatProperty().getFormat();
        return StringUtils.isEmpty(format) ? null : format;
    }
}
