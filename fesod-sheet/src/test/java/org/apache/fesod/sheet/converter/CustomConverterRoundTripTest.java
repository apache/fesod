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

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.converters.ColumnBinding;
import org.apache.fesod.sheet.converters.ReadConverter;
import org.apache.fesod.sheet.converters.WriteConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Verifies custom converters registered through the top-level builder API are actually
 * invoked by the write/read pipeline (not just stored in the registry).
 */
@Tag(Tags.ROUND_TRIP)
class CustomConverterRoundTripTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void shouldApplyRegisteredReadConverterWhenReadingModel(ExcelFormat format) throws Exception {
        // full read chain: builder -> ReadBasicParameter -> holder registry -> column-aware lookup in ConverterUtils
        File file = createTempFile(format);
        RoundTripHelper.write(file, TwoColumnData.class, data());

        CollectingReadListener<TwoColumnData> listener = new CollectingReadListener<>();
        FesodSheet.read(file, TwoColumnData.class, listener)
                .registerReadConverter(new PrefixReadConverter("read:"))
                .sheet()
                .doRead();

        List<TwoColumnData> rows = listener.getRows();
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("read:apple", rows.get(0).getFirst());
        Assertions.assertEquals("read:banana", rows.get(0).getSecond());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldApplyColumnBoundWriteConverterOnlyToItsBoundColumn(ExcelFormat format) throws Exception {
        // the write executor must pass the column index so the bound converter applies to column 0 only
        File file = createTempFile(format);
        FesodSheet.write(file, TwoColumnData.class)
                .registerWriteConverter(new ColumnPrefixWriteConverter())
                .sheet()
                .doWrite(data());

        List<TwoColumnData> rows = RoundTripHelper.read(file, TwoColumnData.class);
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("col0:apple", rows.get(0).getFirst());
        // unbound column keeps the default String converter
        Assertions.assertEquals("banana", rows.get(0).getSecond());
    }

    private static List<TwoColumnData> data() {
        TwoColumnData row = new TwoColumnData();
        row.setFirst("apple");
        row.setSecond("banana");
        return Collections.singletonList(row);
    }

    private static final class PrefixReadConverter implements ReadConverter<String> {

        private final String prefix;

        PrefixReadConverter(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Class<?> supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public String convertToJavaData(
                ReadCellData<?> cellData,
                ExcelContentProperty contentProperty,
                GlobalConfiguration globalConfiguration) {
            return prefix + cellData.getStringValue();
        }
    }

    private static final class ColumnPrefixWriteConverter implements WriteConverter<String>, ColumnBinding {

        @Override
        public Integer columnIndex() {
            return 0;
        }

        @Override
        public Class<?> supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            return new WriteCellData<>("col0:" + value);
        }
    }

    public static class TwoColumnData {

        @ExcelProperty(value = "first", index = 0)
        private String first;

        @ExcelProperty(value = "second", index = 1)
        private String second;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }
    }
}
