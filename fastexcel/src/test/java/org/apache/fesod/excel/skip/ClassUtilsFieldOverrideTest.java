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

package org.apache.fesod.excel.skip;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.annotation.ExcelIgnore;
import org.apache.fesod.excel.annotation.ExcelProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ClassUtilsFieldOverrideTest {

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
        FastExcel.write(file1.toString(), Parent1.class).sheet().doWrite(data1);
        List<String> header1 = readHeader.apply(file1);
        Assertions.assertTrue(header1.contains("parent1Field"), "Parent1 should contain parent1Field");

        // Child1: should NOT contain parent1Field
        Path file2 = tempDir.resolve("child1.xlsx");
        List<Child1> data2 = new ArrayList<>();
        data2.add(new Child1());
        FastExcel.write(file2.toString(), Child1.class).sheet().doWrite(data2);
        List<String> header2 = readHeader.apply(file2);
        Assertions.assertFalse(header2.contains("parent1Field"), "Child1 should NOT contain parent1Field");

        // Parent2: should NOT contain field
        Path file3 = tempDir.resolve("parent2.xlsx");
        List<Parent2> data3 = new ArrayList<>();
        data3.add(new Parent2());
        FastExcel.write(file3.toString(), Parent2.class).sheet().doWrite(data3);
        List<String> header3 = readHeader.apply(file3);
        Assertions.assertFalse(header3.contains("field"), "Parent2 should NOT contain field");

        // Child2: should NOT contain field
        Path file4 = tempDir.resolve("child2.xlsx");
        List<Child2> data4 = new ArrayList<>();
        data4.add(new Child2());
        FastExcel.write(file4.toString(), Child2.class).sheet().doWrite(data4);
        List<String> header4 = readHeader.apply(file4);
        Assertions.assertFalse(header4.contains("field"), "Child2 should NOT contain field");

        // Parent3: should contain parent3Field
        Path file5 = tempDir.resolve("parent3.xlsx");
        List<Parent3> data5 = new ArrayList<>();
        data5.add(new Parent3());
        FastExcel.write(file5.toString(), Parent3.class).sheet().doWrite(data5);
        List<String> header5 = readHeader.apply(file5);
        Assertions.assertTrue(header5.contains("parent3Field"), "Parent3 should contain parent3Field");

        // Child3: should contain child3Field
        Path file6 = tempDir.resolve("child3.xlsx");
        List<Child3> data6 = new ArrayList<>();
        data6.add(new Child3());
        FastExcel.write(file6.toString(), Child3.class).sheet().doWrite(data6);
        List<String> header6 = readHeader.apply(file6);
        Assertions.assertTrue(header6.contains("child3Field"), "Child3 should contain child3Field");

        // Parent4: should NOT contain field
        Path file7 = tempDir.resolve("parent4.xlsx");
        List<Parent4> data7 = new ArrayList<>();
        data7.add(new Parent4());
        FastExcel.write(file7.toString(), Parent4.class).sheet().doWrite(data7);
        List<String> header7 = readHeader.apply(file7);
        Assertions.assertFalse(header7.contains("field"), "Parent4 should NOT contain field");

        // Child4: should contain child4Field
        Path file8 = tempDir.resolve("child4.xlsx");
        List<Child4> data8 = new ArrayList<>();
        data8.add(new Child4());
        FastExcel.write(file8.toString(), Child4.class).sheet().doWrite(data8);
        List<String> header8 = readHeader.apply(file8);
        Assertions.assertTrue(header8.contains("child4Field"), "Child4 should contain child4Field");

        // Parent5: should contain parent5Field
        Path file9 = tempDir.resolve("parent5.xlsx");
        List<Parent5> data9 = new ArrayList<>();
        data9.add(new Parent5());
        FastExcel.write(file9.toString(), Parent5.class).sheet().doWrite(data9);
        List<String> header9 = readHeader.apply(file9);
        Assertions.assertTrue(header9.contains("parent5Field"), "Parent5 should contain parent5Field");

        // Child5: should contain field
        Path file10 = tempDir.resolve("child5.xlsx");
        List<Child5> data10 = new ArrayList<>();
        data10.add(new Child5());
        FastExcel.write(file10.toString(), Child5.class).sheet().doWrite(data10);
        List<String> header10 = readHeader.apply(file10);
        Assertions.assertTrue(header10.contains("field"), "Child5 should contain field");

        // Parent6: should NOT contain field
        Path file11 = tempDir.resolve("parent6.xlsx");
        List<Parent6> data11 = new ArrayList<>();
        data11.add(new Parent6());
        FastExcel.write(file11.toString(), Parent6.class).sheet().doWrite(data11);
        List<String> header11 = readHeader.apply(file11);
        Assertions.assertFalse(header11.contains("field"), "Parent6 should NOT contain field");

        // Child6: should contain field
        Path file12 = tempDir.resolve("child6.xlsx");
        List<Child6> data12 = new ArrayList<>();
        data12.add(new Child6());
        FastExcel.write(file12.toString(), Child6.class).sheet().doWrite(data12);
        List<String> header12 = readHeader.apply(file12);
        Assertions.assertTrue(header12.contains("field"), "Child6 should contain field");
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
