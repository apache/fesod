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

package org.apache.fesod.sheet.converter;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ZonedDateTimeConverterTest {

    @Test
    void writePojoWithZonedDateTimeAndCustomConverter() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MixedMetadataRow row = new MixedMetadataRow();
        row.setStatus("ACTIVE");
        row.setAmount(new BigDecimal("1234.5"));
        row.setCreatedAt(ZonedDateTime.of(2026, 3, 24, 15, 30, 45, 0, ZoneId.of("Asia/Shanghai")));

        Assertions.assertDoesNotThrow(() -> FesodSheet.write(out, MixedMetadataRow.class)
                .registerConverter(new ZonedDateStringConverter())
                .sheet("Sheet1")
                .doWrite(List.of(row)));
    }

    @Getter
    @Setter
    public static class MixedMetadataRow {
        @ExcelProperty(value = "Status", converter = StatusLabelConverter.class)
        private String status;

        @ExcelProperty("Amount")
        @NumberFormat("¥#,##0.00")
        private BigDecimal amount;

        @ExcelProperty("Created At")
        @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
        private ZonedDateTime createdAt;
    }

    public static class StatusLabelConverter implements Converter<String> {
        @Override
        public Class<?> supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            return new WriteCellData<>("STATUS:" + value);
        }
    }

    public static class ZonedDateStringConverter implements Converter<ZonedDateTime> {
        @Override
        public Class<?> supportJavaTypeKey() {
            return ZonedDateTime.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                ZonedDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            String fmt = contentProperty != null && contentProperty.getDateTimeFormatProperty() != null
                    ? contentProperty.getDateTimeFormatProperty().getFormat()
                    : "yyyy-MM-dd HH:mm:ss";
            return new WriteCellData<>(value.toLocalDateTime().format(DateTimeFormatter.ofPattern(fmt)));
        }
    }
}
