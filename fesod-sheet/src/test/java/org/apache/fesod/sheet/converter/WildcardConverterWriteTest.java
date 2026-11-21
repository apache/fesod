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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.RichTextStringData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for custom converters registered with {@code supportExcelTypeKey() == null} (the wildcard key).
 *
 * <p>The wildcard key {@code (JavaType, null)} is looked up directly by the xlsx write path, which uses
 * {@code null} as the target cell data type. CSV writes and fill composition force the target to
 * {@link CellDataTypeEnum#STRING}, so the write executor falls back to the wildcard key when the exact key
 * only holds a built-in default; an explicitly registered converter still wins. See issues #1045 and #1056.
 */
public class WildcardConverterWriteTest extends AbstractExcelTest {

    @Test
    void xlsxWildcardConverterIsApplied() throws Exception {
        File file = new File(tempDir, "wildcard-converter.xlsx");
        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.XLSX)
                .registerConverter(new BooleanYesNoConverter())
                .sheet()
                .doWrite(booleans(Boolean.TRUE, Boolean.FALSE));

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Assertions.assertEquals(
                    "YES", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            Assertions.assertEquals(
                    "NO", workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
        }
    }

    @Test
    void csvWildcardConverterIsApplied() throws Exception {
        File file = new File(tempDir, "wildcard-converter.csv");
        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                .registerConverter(new BooleanYesNoConverter())
                .sheet()
                .doWrite(booleans(Boolean.TRUE, Boolean.FALSE));

        String csvContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(
                csvContent.contains("YES"), "CSV should apply the custom converter, but was: " + csvContent);
        Assertions.assertTrue(
                csvContent.contains("NO"), "CSV should apply the custom converter, but was: " + csvContent);
    }

    @Test
    void csvWithoutCustomConvertersIsUnchanged() throws Exception {
        File file = new File(tempDir, "wildcard-converter-default.csv");
        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                .sheet()
                .doWrite(booleans(Boolean.TRUE, Boolean.FALSE));

        String csvContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(
                csvContent.contains("true"), "Default CSV output should be unchanged, but was: " + csvContent);
        Assertions.assertTrue(
                csvContent.contains("false"), "Default CSV output should be unchanged, but was: " + csvContent);
    }

    @Test
    void csvExplicitStringKeyConverterIsUnaffected() throws Exception {
        File file = new File(tempDir, "wildcard-converter-explicit.csv");
        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                .registerConverter(new BooleanExplicitStringConverter())
                .sheet()
                .doWrite(booleans(Boolean.TRUE));

        String csvContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(
                csvContent.contains("explicit-yes"),
                "Explicit STRING-key converter should win, but was: " + csvContent);
    }

    @Test
    void csvExplicitStringKeyRegisteredBeforeWildcardStillWins() throws Exception {
        File file = new File(tempDir, "wildcard-converter-explicit-first.csv");
        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                .registerConverter(new BooleanExplicitStringConverter())
                .registerConverter(new BooleanYesNoConverter())
                .sheet()
                .doWrite(booleans(Boolean.TRUE, Boolean.FALSE));

        String csvContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(
                csvContent.contains("explicit-yes"),
                "Explicit STRING-key converter registered before the wildcard should still win, but was: "
                        + csvContent);
    }

    @Test
    void csvExplicitStringKeyRegisteredAfterWildcardStillWins() throws Exception {
        File file = new File(tempDir, "wildcard-converter-explicit-last.csv");
        FesodSheet.write(file)
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                .registerConverter(new BooleanYesNoConverter())
                .registerConverter(new BooleanExplicitStringConverter())
                .sheet()
                .doWrite(booleans(Boolean.TRUE, Boolean.FALSE));

        String csvContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        Assertions.assertTrue(
                csvContent.contains("explicit-yes") && csvContent.contains("explicit-no"),
                "Explicit STRING-key converter registered after the wildcard should still win, but was: " + csvContent);
    }

    @Test
    void wildcardConverterKeepsTheWildcardKeyRegistration() throws Exception {
        File file = new File(tempDir, "wildcard-converter-map.csv");
        BooleanYesNoConverter converter = new BooleanYesNoConverter();
        try (ExcelWriter excelWriter = FesodSheet.write(file)
                .excelType(ExcelTypeEnum.CSV)
                .registerConverter(converter)
                .build()) {
            excelWriter.write(
                    booleans(Boolean.TRUE),
                    new ExcelWriterSheetBuilder().sheetNo(0).build());
            Map<ConverterKeyBuild.ConverterKey, Converter<?>> workbookMap =
                    excelWriter.writeContext().writeWorkbookHolder().converterMap();
            Map<ConverterKeyBuild.ConverterKey, Converter<?>> sheetMap =
                    excelWriter.writeContext().writeSheetHolder().converterMap();

            ConverterKeyBuild.ConverterKey wildcardKey = ConverterKeyBuild.buildKey(Boolean.class, null);
            ConverterKeyBuild.ConverterKey stringKey =
                    ConverterKeyBuild.buildKey(Boolean.class, CellDataTypeEnum.STRING);
            Converter<?> defaultStringConverter =
                    DefaultConverterLoader.loadDefaultWriteConverter().get(stringKey);
            for (Map<ConverterKeyBuild.ConverterKey, Converter<?>> converterMap :
                    Arrays.asList(workbookMap, sheetMap)) {
                Assertions.assertSame(converter, converterMap.get(wildcardKey));
                Assertions.assertSame(defaultStringConverter, converterMap.get(stringKey));
            }
        }
    }

    @Test
    void lastWildcardRegistrationWinsInBothFormats() throws Exception {
        List<List<Boolean>> rows = Collections.singletonList(Collections.singletonList(Boolean.TRUE));

        File csv = new File(tempDir, "two-wildcards.csv");
        FesodSheet.write(csv)
                .excelType(ExcelTypeEnum.CSV)
                .charset(StandardCharsets.UTF_8)
                .registerConverter(new TaggedConverter("A"))
                .registerConverter(new TaggedConverter("B"))
                .sheet()
                .doWrite(rows);
        String csvContent = new String(Files.readAllBytes(csv.toPath()), StandardCharsets.UTF_8);

        File xlsx = new File(tempDir, "two-wildcards.xlsx");
        FesodSheet.write(xlsx)
                .excelType(ExcelTypeEnum.XLSX)
                .registerConverter(new TaggedConverter("A"))
                .registerConverter(new TaggedConverter("B"))
                .sheet()
                .doWrite(rows);
        String xlsxValue;
        try (Workbook workbook = WorkbookFactory.create(xlsx)) {
            xlsxValue = workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
        }

        Assertions.assertEquals("result-B", xlsxValue);
        Assertions.assertTrue(
                csvContent.contains("result-B"), "CSV must match xlsx of the same code, but was: " + csvContent);
    }

    @Test
    void fillCompositionStringifiesWildcardConverterResults() throws Exception {
        File template = new File(tempDir, "wildcard-fill-template.xlsx");
        FesodSheet.write(template).sheet().doWrite(Collections.singletonList(Collections.singletonList("{a} / {b}")));

        File file = new File(tempDir, "wildcard-fill.xlsx");
        Map<String, Object> data = new HashMap<>();
        data.put("a", LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        data.put("b", Boolean.FALSE);
        FesodSheet.write(file)
                .withTemplate(template)
                .registerConverter(new LocalDateTimeWildcardConverter())
                .registerConverter(new BooleanYesNoConverter())
                .sheet()
                .doFill(data);

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Assertions.assertEquals(
                    "2024-01-02 03:04:05 / NO",
                    workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        }
    }

    @Test
    void fillCompositionAppendsRichTextWildcardConverterResult() throws Exception {
        File template = new File(tempDir, "wildcard-rich-text-fill-template.xlsx");
        FesodSheet.write(template).sheet().doWrite(Collections.singletonList(Collections.singletonList("{a}!")));

        File file = new File(tempDir, "wildcard-rich-text-fill.xlsx");
        Map<String, Object> data = new HashMap<>();
        data.put("a", "ignored");
        FesodSheet.write(file)
                .withTemplate(template)
                .registerConverter(new StringRichTextConverter())
                .sheet()
                .doFill(data);

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Assertions.assertEquals(
                    "rich!", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        }
    }

    private static List<List<Boolean>> booleans(Boolean... values) {
        List<List<Boolean>> rows = new java.util.ArrayList<>();
        for (Boolean value : values) {
            rows.add(Collections.singletonList(value));
        }
        return rows;
    }

    /** Custom converter returning {@code null} from {@code supportExcelTypeKey()} (the wildcard key). */
    public static class BooleanYesNoConverter implements Converter<Boolean> {
        @Override
        public Class<?> supportJavaTypeKey() {
            return Boolean.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                Boolean value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            if (Boolean.TRUE.equals(value)) {
                return new WriteCellData<>("YES");
            }
            if (Boolean.FALSE.equals(value)) {
                return new WriteCellData<>("NO");
            }
            return new WriteCellData<>("");
        }
    }

    /** Custom converter explicitly declaring the {@link CellDataTypeEnum#STRING} key. */
    public static class BooleanExplicitStringConverter implements Converter<Boolean> {
        @Override
        public Class<?> supportJavaTypeKey() {
            return Boolean.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                Boolean value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            if (Boolean.TRUE.equals(value)) {
                return new WriteCellData<>("explicit-yes");
            }
            return new WriteCellData<>("explicit-no");
        }
    }

    /** Wildcard converter tagging its output, used to tell which registration wins. */
    public static class TaggedConverter implements Converter<Boolean> {
        private final String tag;

        public TaggedConverter(String tag) {
            this.tag = tag;
        }

        @Override
        public Class<?> supportJavaTypeKey() {
            return Boolean.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                Boolean value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            return new WriteCellData<>("result-" + tag);
        }
    }

    /** Wildcard converter returning a DATE cell, as converters targeting xlsx commonly do. */
    public static class LocalDateTimeWildcardConverter implements Converter<LocalDateTime> {
        @Override
        public Class<?> supportJavaTypeKey() {
            return LocalDateTime.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                LocalDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            return new WriteCellData<>(value);
        }
    }

    /** Wildcard converter returning a rich text cell. */
    public static class StringRichTextConverter implements Converter<String> {
        @Override
        public Class<?> supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            WriteCellData<String> cellData = new WriteCellData<>(CellDataTypeEnum.RICH_TEXT_STRING);
            cellData.setRichTextStringDataValue(new RichTextStringData("rich"));
            return cellData;
        }
    }
}
