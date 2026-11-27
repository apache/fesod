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

package org.apache.fesod.sheet.fill;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.WriteDirectionEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.fesod.sheet.write.metadata.fill.FillWrapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
class NestedObjectFillTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void fillsNestedBeansAndRetainsLeafAnnotations(ExcelFormat format) throws Exception {
        File template = template(format, "{.child.name}", "Value {.child.name}", "{.child.date}");
        File output = createTempFile("nested-beans", format);
        FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(Arrays.asList(new Record(new Child("alpha")), new Record(new Child("beta"))));

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Sheet sheet = workbook.getSheetAt(0);
            Assertions.assertEquals("ALPHA", sheet.getRow(0).getCell(0).getStringCellValue());
            Assertions.assertEquals("Value ALPHA", sheet.getRow(0).getCell(1).getStringCellValue());
            Assertions.assertEquals("BETA", sheet.getRow(1).getCell(0).getStringCellValue());
            Assertions.assertEquals(
                    "yyyy/MM/dd", sheet.getRow(0).getCell(2).getCellStyle().getDataFormatString());
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void fillsNamedNestedMapsAndKeepsExactKeysFirst(ExcelFormat format) throws Exception {
        File template = template(format, "{items.child.name}", "Value {items.child.name}", "{items.child.meta.code}");
        File output = createTempFile("nested-maps", format);
        Map<String, Object> row = new HashMap<>();
        Map<String, Object> child = new HashMap<>();
        child.put("name", "nested");
        child.put("meta", Collections.singletonMap("code", 7));
        row.put("child", child);
        row.put("child.name", "literal");

        FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(new FillWrapper("items", Collections.singletonList(row)));

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Row filled = workbook.getSheetAt(0).getRow(0);
            Assertions.assertEquals("literal", filled.getCell(0).getStringCellValue());
            Assertions.assertEquals("Value literal", filled.getCell(1).getStringCellValue());
            Assertions.assertEquals(7, filled.getCell(2).getNumericCellValue());
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void clearsMissingAndNullNestedProperties(ExcelFormat format) throws Exception {
        File template = template(format, "{.child.name}", "Value {.child.name}", "{.child.missing}");
        File output = createTempFile("nested-nulls", format);
        Map<String, Object> exactNull = new HashMap<>();
        exactNull.put("child", new Child("must-not-be-used"));
        exactNull.put("child.name", null);
        FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(Arrays.asList(new Record(null), Collections.emptyMap(), exactNull));

        try (Workbook workbook = WorkbookFactory.create(output)) {
            for (int row = 0; row < 3; row++) {
                Row filled = workbook.getSheetAt(0).getRow(row);
                Assertions.assertEquals("", filled.getCell(0).getStringCellValue());
                Assertions.assertEquals("Value ", filled.getCell(1).getStringCellValue());
                Assertions.assertEquals("", filled.getCell(2).getStringCellValue());
            }
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void fillsMixedMapAndBeanPathsHorizontally(ExcelFormat format) throws Exception {
        File template = template(format, "{.child.name}");
        File output = createTempFile("nested-horizontal", format);
        FillConfig config =
                FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        FesodSheet.write(output)
                .withTemplate(template)
                .sheet()
                .doFill(
                        Arrays.asList(
                                Collections.singletonMap("child", new Child("alpha")), new Record(new Child("beta"))),
                        config);

        try (Workbook workbook = WorkbookFactory.create(output)) {
            Row filled = workbook.getSheetAt(0).getRow(0);
            Assertions.assertEquals("ALPHA", filled.getCell(0).getStringCellValue());
            Assertions.assertEquals("BETA", filled.getCell(1).getStringCellValue());
        }
    }

    private File template(ExcelFormat format, String... placeholders) throws Exception {
        File file = createTempFile("nested-template", format);
        try (Workbook workbook = format == ExcelFormat.XLS ? new HSSFWorkbook() : new XSSFWorkbook();
                FileOutputStream output = new FileOutputStream(file)) {
            Row row = workbook.createSheet().createRow(0);
            for (int i = 0; i < placeholders.length; i++) {
                row.createCell(i).setCellValue(placeholders[i]);
            }
            workbook.write(output);
        }
        return file;
    }

    @Getter
    @AllArgsConstructor
    public static class Record {
        private final Child child;
    }

    @Getter
    public static class Child {
        @ExcelProperty(converter = UpperCaseConverter.class)
        private final String name;

        @DateTimeFormat("yyyy/MM/dd")
        private final LocalDate date = LocalDate.of(2026, 10, 20);

        public Child(String name) {
            this.name = name;
        }
    }

    public static class UpperCaseConverter implements Converter<String> {
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
                String value, ExcelContentProperty property, GlobalConfiguration configuration) {
            return new WriteCellData<>(value.toUpperCase(Locale.ROOT));
        }
    }
}
