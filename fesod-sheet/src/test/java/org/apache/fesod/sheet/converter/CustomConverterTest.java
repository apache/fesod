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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.ROUND_TRIP)
public class CustomConverterTest extends AbstractExcelTest {

    @Test
    void converterMapTest() {
        File converterCsvFile10 = new File(tempDir, "converter10.csv");
        TimestampStringConverter timestampStringConverter = new TimestampStringConverter();
        TimestampNumberConverter timestampNumberConverter = new TimestampNumberConverter();
        ExcelWriter excelWriter = FesodSheet.write(converterCsvFile10)
                .registerConverter(timestampStringConverter)
                .registerConverter(timestampNumberConverter)
                .build();
        Map<ConverterKeyBuild.ConverterKey, Converter<?>> converterMap =
                excelWriter.writeContext().currentWriteHolder().converterMap();
        excelWriter.write(data(), new ExcelWriterSheetBuilder().sheetNo(0).build());
        excelWriter.finish();
        Assertions.assertTrue(converterMap.containsKey(ConverterKeyBuild.buildKey(
                timestampStringConverter.supportJavaTypeKey(), timestampStringConverter.supportExcelTypeKey())));
        Assertions.assertTrue(converterMap.containsKey(ConverterKeyBuild.buildKey(
                timestampNumberConverter.supportJavaTypeKey(), timestampNumberConverter.supportExcelTypeKey())));
    }

    @Test
    void writeCsv() throws Exception {
        writeFile(new File(tempDir, "converter10.csv"));
    }

    @Test
    void writeXls() throws Exception {
        writeFile(new File(tempDir, "converter11.xls"));
    }

    @Test
    void writeXlsx() throws Exception {
        writeFile(new File(tempDir, "converter12.xlsx"));
    }

    @Test
    void globalConverterInSheetHolder() {
        File converterExcelFile13 = new File(tempDir, "converter13.xlsx");
        TimestampStringConverter timestampStringConverter = new TimestampStringConverter();
        ExcelWriter excelWriter = FesodSheet.write(converterExcelFile13)
                .registerConverter(timestampStringConverter)
                .build();
        excelWriter.write(data(), new ExcelWriterSheetBuilder().sheetNo(0).build());
        WriteSheetHolder sheetHolder = excelWriter.writeContext().writeSheetHolder();
        Map<ConverterKeyBuild.ConverterKey, Converter<?>> sheetConverterMap = sheetHolder.converterMap();
        excelWriter.finish();
        Assertions.assertTrue(sheetConverterMap.containsKey(ConverterKeyBuild.buildKey(
                timestampStringConverter.supportJavaTypeKey(), timestampStringConverter.supportExcelTypeKey())));
    }

    @Test
    void globalConverterWriteWithoutFieldLevelConverter() throws Exception {
        FesodSheet.write(new File(tempDir, "converter14.csv"))
                .registerConverter(new TimestampStringConverter())
                .sheet()
                .doWrite(globalData());
    }

    @Test
    void fieldLevelConverterTakesPrecedenceOverRegisteredConverter() throws Exception {
        File converterCsvFile15 = new File(tempDir, "converter15.csv");
        FieldLevelConverterWriteData writeData = new FieldLevelConverterWriteData();
        writeData.setFieldValue("value");
        writeData.setRegisteredValue("value");
        List<FieldLevelConverterWriteData> list = new ArrayList<>();
        list.add(writeData);

        FesodSheet.write(converterCsvFile15, FieldLevelConverterWriteData.class)
                .registerConverter(new RegisteredStringConverter())
                .sheet()
                .doWrite(list);

        String csvContent = new String(Files.readAllBytes(converterCsvFile15.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(csvContent.contains("field:value,registered:value"));
    }

    private void writeFile(File file) {
        FesodSheet.write(file)
                .registerConverter(new TimestampNumberConverter())
                .registerConverter(new TimestampStringConverter())
                .sheet()
                .doWrite(data());
    }

    private List<GlobalConverterWriteData> globalData() {
        List<GlobalConverterWriteData> list = new ArrayList<>();
        GlobalConverterWriteData writeData = new GlobalConverterWriteData();
        writeData.setTimestampData(Timestamp.valueOf("2020-01-01 01:00:00"));
        list.add(writeData);
        return list;
    }

    private List<CustomConverterWriteData> data() {
        return TestDataBuilder.customConverterWriteData();
    }

    public static class FieldLevelConverterWriteData {
        @ExcelProperty(value = "fieldValue", converter = FieldLevelStringConverter.class)
        private String fieldValue;

        @ExcelProperty("registeredValue")
        private String registeredValue;

        public String getFieldValue() {
            return fieldValue;
        }

        public void setFieldValue(String fieldValue) {
            this.fieldValue = fieldValue;
        }

        public String getRegisteredValue() {
            return registeredValue;
        }

        public void setRegisteredValue(String registeredValue) {
            this.registeredValue = registeredValue;
        }
    }

    public static class FieldLevelStringConverter implements Converter<String> {
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
            return new WriteCellData<>("field:" + value);
        }
    }

    public static class RegisteredStringConverter implements Converter<String> {
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
            return new WriteCellData<>("registered:" + value);
        }
    }
}
