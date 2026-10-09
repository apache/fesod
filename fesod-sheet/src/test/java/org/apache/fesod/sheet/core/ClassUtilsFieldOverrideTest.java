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

package org.apache.fesod.sheet.core;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.poi.HorizontalAlignmentEnum;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;

@Tag(Tags.ROUND_TRIP)
public class ClassUtilsFieldOverrideTest extends AbstractExcelTest {

    @Getter
    @Setter
    static class Parent1 {

        @ExcelProperty("parent1Field")
        private String field;
    }

    @Getter
    @Setter
    static class Child1 extends Parent1 {

        @ExcelIgnore
        private String field;
    }

    @Getter
    @Setter
    static class Parent2 {

        @ExcelIgnore
        private String field;
    }

    @Getter
    @Setter
    static class Child2 extends Parent2 {

        @ExcelIgnore
        private String field;
    }

    @Getter
    @Setter
    static class Parent3 {

        @ExcelProperty("parent3Field")
        private String field;
    }

    @Getter
    @Setter
    static class Child3 extends Parent3 {

        @ExcelProperty("child3Field")
        private String field;
    }

    @Getter
    @Setter
    static class Parent4 {

        @ExcelIgnore
        private String field;
    }

    @Getter
    @Setter
    static class Child4 extends Parent4 {

        @ExcelProperty("child4Field")
        private String field;
    }

    @Getter
    @Setter
    static class Parent5 {

        @ExcelProperty("parent5Field")
        private String field;
    }

    @Getter
    @Setter
    static class Child5 extends Parent5 {

        private String field;
    }

    @Getter
    @Setter
    static class Parent6 {

        @ExcelIgnore
        private String field;
    }

    @Getter
    @Setter
    static class Child6 extends Parent6 {

        private String field;
    }

    @Test
    public void validateHeaderWithExcelIgnore(@TempDir Path tempDir) {
        Function<Path, List<String>> readHeader = extractExcelHeader();

        // Parent1: should contain parent1Field
        Path file1 = tempDir.resolve("parent1.xlsx");
        List<Parent1> data1 = new ArrayList<>();
        data1.add(new Parent1());
        FesodSheet.write(file1.toString(), Parent1.class).sheet().doWrite(data1);
        List<String> header1 = readHeader.apply(file1);
        Assertions.assertTrue(header1.contains("parent1Field"), "Parent1 should contain parent1Field");

        // Child1: should NOT contain parent1Field
        Path file2 = tempDir.resolve("child1.xlsx");
        List<Child1> data2 = new ArrayList<>();
        data2.add(new Child1());
        FesodSheet.write(file2.toString(), Child1.class).sheet().doWrite(data2);
        List<String> header2 = readHeader.apply(file2);
        Assertions.assertFalse(header2.contains("parent1Field"), "Child1 should NOT contain parent1Field");

        // Parent2: should NOT contain field
        Path file3 = tempDir.resolve("parent2.xlsx");
        List<Parent2> data3 = new ArrayList<>();
        data3.add(new Parent2());
        FesodSheet.write(file3.toString(), Parent2.class).sheet().doWrite(data3);
        List<String> header3 = readHeader.apply(file3);
        Assertions.assertFalse(header3.contains("field"), "Parent2 should NOT contain field");

        // Child2: should NOT contain field
        Path file4 = tempDir.resolve("child2.xlsx");
        List<Child2> data4 = new ArrayList<>();
        data4.add(new Child2());
        FesodSheet.write(file4.toString(), Child2.class).sheet().doWrite(data4);
        List<String> header4 = readHeader.apply(file4);
        Assertions.assertFalse(header4.contains("field"), "Child2 should NOT contain field");

        // Parent3: should contain parent3Field
        Path file5 = tempDir.resolve("parent3.xlsx");
        List<Parent3> data5 = new ArrayList<>();
        data5.add(new Parent3());
        FesodSheet.write(file5.toString(), Parent3.class).sheet().doWrite(data5);
        List<String> header5 = readHeader.apply(file5);
        Assertions.assertTrue(header5.contains("parent3Field"), "Parent3 should contain parent3Field");

        // Child3: should contain child3Field
        Path file6 = tempDir.resolve("child3.xlsx");
        List<Child3> data6 = new ArrayList<>();
        data6.add(new Child3());
        FesodSheet.write(file6.toString(), Child3.class).sheet().doWrite(data6);
        List<String> header6 = readHeader.apply(file6);
        Assertions.assertTrue(header6.contains("child3Field"), "Child3 should contain child3Field");

        // Parent4: should NOT contain field
        Path file7 = tempDir.resolve("parent4.xlsx");
        List<Parent4> data7 = new ArrayList<>();
        data7.add(new Parent4());
        FesodSheet.write(file7.toString(), Parent4.class).sheet().doWrite(data7);
        List<String> header7 = readHeader.apply(file7);
        Assertions.assertFalse(header7.contains("field"), "Parent4 should NOT contain field");

        // Child4: should contain child4Field
        Path file8 = tempDir.resolve("child4.xlsx");
        List<Child4> data8 = new ArrayList<>();
        data8.add(new Child4());
        FesodSheet.write(file8.toString(), Child4.class).sheet().doWrite(data8);
        List<String> header8 = readHeader.apply(file8);
        Assertions.assertTrue(header8.contains("child4Field"), "Child4 should contain child4Field");

        // Parent5: should contain parent5Field
        Path file9 = tempDir.resolve("parent5.xlsx");
        List<Parent5> data9 = new ArrayList<>();
        data9.add(new Parent5());
        FesodSheet.write(file9.toString(), Parent5.class).sheet().doWrite(data9);
        List<String> header9 = readHeader.apply(file9);
        Assertions.assertTrue(header9.contains("parent5Field"), "Parent5 should contain parent5Field");

        // Child5: should contain field
        Path file10 = tempDir.resolve("child5.xlsx");
        List<Child5> data10 = new ArrayList<>();
        data10.add(new Child5());
        FesodSheet.write(file10.toString(), Child5.class).sheet().doWrite(data10);
        List<String> header10 = readHeader.apply(file10);
        Assertions.assertTrue(header10.contains("field"), "Child5 should contain field");

        // Parent6: should NOT contain field
        Path file11 = tempDir.resolve("parent6.xlsx");
        List<Parent6> data11 = new ArrayList<>();
        data11.add(new Parent6());
        FesodSheet.write(file11.toString(), Parent6.class).sheet().doWrite(data11);
        List<String> header11 = readHeader.apply(file11);
        Assertions.assertFalse(header11.contains("field"), "Parent6 should NOT contain field");

        // Child6: should contain field
        Path file12 = tempDir.resolve("child6.xlsx");
        List<Child6> data12 = new ArrayList<>();
        data12.add(new Child6());
        FesodSheet.write(file12.toString(), Child6.class).sheet().doWrite(data12);
        List<String> header12 = readHeader.apply(file12);
        Assertions.assertTrue(header12.contains("field"), "Child6 should contain field");
    }

    public static class ParentStringConverter implements Converter<String> {

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
                throws Exception {
            return new WriteCellData<>("Parent: " + value);
        }
    }

    public static class ChildStringConverter implements Converter<String> {

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public WriteCellData<?> convertToExcelData(
                String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
                throws Exception {
            return new WriteCellData<>("Child: " + value);
        }
    }

    @Setter
    @Getter
    static class ParentWithAnnotation {
        @DateTimeFormat("yyyy")
        Date date;

        @NumberFormat("#.##%")
        Double doubleValue;

        @DateTimeFormat("yyyy")
        Date childDate;

        @NumberFormat("#.##%")
        Double childDoubleValue;

        @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
        String string1;

        @ContentFontStyle(fontHeightInPoints = 30)
        String string2;

        @ExcelProperty(converter = ParentStringConverter.class)
        String string3;

        @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
        String childString1;

        @ContentFontStyle(fontHeightInPoints = 30)
        String childString2;

        @ExcelProperty(converter = ParentStringConverter.class)
        String childString3;
    }

    @Setter
    @Getter
    static class Child extends ParentWithAnnotation {
        Date childDate;
        Double childDoubleValue;
        String childString1;
        String childString2;
        String childString3;

        static List<Child> data() {
            List<Child> data = new ArrayList<>();
            Child child = new Child();
            child.setDate(new Date());
            child.setDoubleValue(0.5D);
            child.setChildDate(new Date());
            child.setChildDoubleValue(0.5D);
            child.setString1("string1");
            child.setString2("string2");
            child.setString3("string3");
            child.setChildString1("childString1");
            child.setChildString2("childString2");
            child.setChildString3("childString3");
            data.add(child);
            return data;
        }
    }

    @Setter
    @Getter
    static class ChildWithAnnotation extends ParentWithAnnotation {
        @DateTimeFormat("yyyy-MM-dd")
        Date childDate;

        @NumberFormat("#.00")
        Double childDoubleValue;

        @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
        String childString1;

        @ContentFontStyle(fontHeightInPoints = 25)
        String childString2;

        @ExcelProperty(converter = ChildStringConverter.class)
        String childString3;

        static List<ChildWithAnnotation> data() {
            List<ChildWithAnnotation> data = new ArrayList<>();
            ChildWithAnnotation child = new ChildWithAnnotation();
            child.setDate(new Date());
            child.setDoubleValue(0.5D);
            child.setChildDate(new Date());
            child.setChildDoubleValue(0.5D);
            child.setString1("string1");
            child.setString2("string2");
            child.setString3("string3");
            child.setChildString1("childString1");
            child.setChildString2("childString2");
            child.setChildString3("childString3");
            data.add(child);
            return data;
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY)
    void test_fieldShadowing_subclassWithoutAnnotation(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        DataFormatter formatter = new DataFormatter();

        // Subclass overrides property without declaring annotations.
        // Subclass attributes completely shadow parent class attributes; parent class annotations should not take
        // effect/be inherited.
        FesodSheet.write(file).head(Child.class).sheet().doWrite(Child.data());

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .row(1)
                    // col 0 = childDate: subclass shadows parent, no @DateTimeFormat inherited
                    .cell(0)
                    .hasDataFormatString(DateUtils.defaultDateFormat)
                    .and()
                    // col 1 = childDoubleValue: subclass shadows parent, no @NumberFormat inherited
                    .cell(1)
                    .satisfies(cell -> {
                        String formatCellValue = formatter.formatCellValue(cell);
                        Assertions.assertEquals("0.5", formatCellValue);
                    })
                    .and()
                    // col 2 = childString1: subclass shadows parent, no @ContentStyle inherited
                    .cell(2)
                    .satisfies(cell -> {
                        Assertions.assertEquals("childString1", cell.getStringCellValue());
                        Assertions.assertNotEquals(
                                HorizontalAlignment.CENTER, cell.getCellStyle().getAlignment());
                    })
                    .and()
                    // col 3 = childString2: subclass shadows parent, no @ContentFontStyle inherited
                    .cell(3)
                    .satisfies(cell -> Assertions.assertEquals("childString2", cell.getStringCellValue()))
                    .and()
                    // col 4 = childString3: subclass shadows parent, no converter inherited
                    .cell(4)
                    .satisfies(cell -> Assertions.assertEquals("childString3", cell.getStringCellValue()))
                    .and()
                    // col 5 = date: parent field retains @DateTimeFormat("yyyy")
                    .cell(5)
                    .hasDataFormatString("yyyy")
                    .and()
                    // col 6 = doubleValue: parent field retains @NumberFormat("#.##%")
                    .cell(6)
                    .satisfies(cell -> {
                        String formatCellValue = formatter.formatCellValue(cell);
                        Assertions.assertEquals("50%", formatCellValue);
                    })
                    .and()
                    // col 7 = string1: parent field retains @ContentStyle(CENTER)
                    .cell(7)
                    .satisfies(cell -> {
                        Assertions.assertEquals("string1", cell.getStringCellValue());
                        Assertions.assertEquals(
                                HorizontalAlignment.CENTER, cell.getCellStyle().getAlignment());
                    })
                    .and()
                    // col 8 = string2: parent field retains @ContentFontStyle(fontHeightInPoints = 30)
                    .cell(8)
                    .satisfies(cell -> Assertions.assertEquals("string2", cell.getStringCellValue()))
                    .hasFontSize((short) 30)
                    .and()
                    // col 9 = string3: parent field retains @ExcelProperty converter
                    .cell(9)
                    .satisfies(cell -> Assertions.assertEquals("Parent: string3", cell.getStringCellValue()));
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY)
    void test_fieldShadowing_subclassAnnotationPrecedence(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        DataFormatter formatter = new DataFormatter();

        // Subclass overrides a property and explicitly declares a subclass-specific annotations.
        // Directly use the annotation format on subclass attributes
        FesodSheet.write(file).head(ChildWithAnnotation.class).sheet().doWrite(ChildWithAnnotation.data());

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .row(1)
                    // col 0 = childDate: subclass @DateTimeFormat("yyyy-MM-dd") takes precedence
                    .cell(0)
                    .hasDataFormatString("yyyy-MM-dd")
                    .and()
                    // col 1 = childDoubleValue: subclass @NumberFormat("#.00") takes precedence
                    .cell(1)
                    .satisfies(cell -> {
                        String formatCellValue = formatter.formatCellValue(cell);
                        Assertions.assertEquals(".50", formatCellValue);
                    })
                    .and()
                    // col 2 = childString1: subclass @ContentStyle(LEFT) takes precedence
                    .cell(2)
                    .satisfies(cell -> {
                        Assertions.assertEquals("childString1", cell.getStringCellValue());
                        Assertions.assertEquals(
                                HorizontalAlignment.LEFT, cell.getCellStyle().getAlignment());
                    })
                    .and()
                    // col 3 = childString2: subclass @ContentFontStyle(25) takes precedence
                    .cell(3)
                    .satisfies(cell -> Assertions.assertEquals("childString2", cell.getStringCellValue()))
                    .hasFontSize((short) 25)
                    .and()
                    // col 4 = childString3: subclass @ExcelProperty converter takes precedence
                    .cell(4)
                    .satisfies(cell -> Assertions.assertEquals("Child: childString3", cell.getStringCellValue()))
                    .and()
                    // col 5 = date: parent @DateTimeFormat("yyyy") retained
                    .cell(5)
                    .hasDataFormatString("yyyy")
                    .and()
                    // col 6 = doubleValue: parent @NumberFormat("#.##%") retained
                    .cell(6)
                    .satisfies(cell -> {
                        String formatCellValue = formatter.formatCellValue(cell);
                        Assertions.assertEquals("50%", formatCellValue);
                    })
                    .and()
                    // col 7 = string1: parent field retains @ContentStyle(CENTER)
                    .cell(7)
                    .satisfies(cell -> {
                        Assertions.assertEquals("string1", cell.getStringCellValue());
                        Assertions.assertEquals(
                                HorizontalAlignment.CENTER, cell.getCellStyle().getAlignment());
                    })
                    .and()
                    // col 8 = string2: parent field retains @ContentFontStyle(fontHeightInPoints = 30)
                    .cell(8)
                    .satisfies(cell -> Assertions.assertEquals("string2", cell.getStringCellValue()))
                    .hasFontSize((short) 30)
                    .and()
                    // col 9 = string3: parent field retains @ExcelProperty converter
                    .cell(9)
                    .satisfies(cell -> Assertions.assertEquals("Parent: string3", cell.getStringCellValue()));
        }
    }

    private static Function<Path, List<String>> extractExcelHeader() {
        // Helper to read first row (header) from generated file
        Function<Path, List<String>> readHeader = path -> {
            try (java.io.InputStream is = java.nio.file.Files.newInputStream(path)) {
                org.apache.poi.ss.usermodel.Workbook wb = org.apache.poi.ss.usermodel.WorkbookFactory.create(is);
                org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(0);
                List<String> headers = new ArrayList<>();
                for (org.apache.poi.ss.usermodel.Cell cell : row) {
                    headers.add(cell.getStringCellValue());
                }
                wb.close();
                return headers;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return readHeader;
    }
}
