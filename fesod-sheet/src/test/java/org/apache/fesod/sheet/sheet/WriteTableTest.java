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

package org.apache.fesod.sheet.sheet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import lombok.Data;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.builder.ExcelWriterSheetBuilder;
import org.apache.fesod.sheet.write.builder.ExcelWriterTableBuilder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Tag(Tags.ROUND_TRIP)
public class WriteTableTest extends AbstractExcelTest {

    @Data
    @ExcelIgnoreUnannotated
    public static class WriteSheetData {
        @ExcelProperty("Title")
        private String string;
    }

    private static final List<List<Integer>> OFFSETS = Arrays.asList(
            Arrays.asList(0, 0, 0), Arrays.asList(0, 1, 3), Arrays.asList(1, 0, 2), Arrays.asList(2, 3, 0));

    static Stream<Arguments> testData() {
        return Stream.of(Arguments.of(ExcelFormat.XLSX, OFFSETS), Arguments.of(ExcelFormat.XLS, OFFSETS));
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void testWriteTable(ExcelFormat excelFormat, List<List<Integer>> offsets) throws Exception {
        int n = offsets.size();

        File testFile = createTempFile(excelFormat);

        try (ExcelWriter write = FesodSheet.write(testFile)
                .excelType(excelFormat.toExcelTypeEnum())
                .build()) {
            writeSheets(write, offsets, 0, "T", true);
            writeSheets(write, offsets, n, "U", false);
        }

        Assertions.assertTrue(testFile.exists(), "Written file should exist");
        Assertions.assertTrue(testFile.length() > 0, "Written file should not be empty");

        try (Workbook workbook = WorkbookFactory.create(testFile)) {
            Iterator<Sheet> it = workbook.sheetIterator();
            for (int i = 0; i < n; i++) {
                Assertions.assertTrue(it.hasNext(), "titled sheet " + i + " should exist");
                verifyRows(it.next(), true, offsets.get(i));
            }
            for (int i = 0; i < n; i++) {
                Assertions.assertTrue(it.hasNext(), "untitled sheet " + i + " should exist");
                verifyRows(it.next(), false, offsets.get(i));
            }
        }
    }

    private static void writeSheets(
            ExcelWriter write, List<List<Integer>> offsets, int startSheetNo, String namePrefix, boolean isTitled) {
        int n = offsets.size();
        for (int i = 0; i < n; i++) {
            ExcelWriterSheetBuilder sheet =
                    FesodSheet.writerSheet().sheetNo(startSheetNo + i).sheetName(namePrefix + i);
            List<Integer> rowOffsets = offsets.get(i);
            for (int j = 0; j < rowOffsets.size(); j++) {
                ExcelWriterTableBuilder table = FesodSheet.writerTable()
                        .relativeHeadRowIndex(rowOffsets.get(j))
                        .tableNo(j);
                if (isTitled) {
                    table.head(WriteSheetData.class);
                }
                write.write(getList((char) ('A' + j)), sheet.build(), table.build());
            }
        }
    }

    private static void verifyRows(Sheet sheet, boolean isTitled, List<Integer> offsets) {
        int rowIdx = 0;
        char prefix = 'A';
        for (int offset : offsets) {
            if (isTitled) {
                Row headerRow = sheet.getRow(rowIdx + offset);
                Assertions.assertNotNull(headerRow, "Header row " + (rowIdx + offset) + " missing");
                Assertions.assertEquals(
                        "Title",
                        headerRow.getCell(0).getStringCellValue(),
                        "Row " + (rowIdx + offset) + " should be header");
                rowIdx += offset + 1;
            } else {
                rowIdx += offset;
            }
            for (int j = 0; j < 2; j++) {
                Row row = sheet.getRow(rowIdx + j);
                Assertions.assertNotNull(row, "Data row " + (rowIdx + j) + " missing");
                Assertions.assertEquals(
                        prefix + "-" + j, row.getCell(0).getStringCellValue(), "Row " + (rowIdx + j) + " mismatch");
            }
            rowIdx += 2;
            prefix++;
        }
    }

    private static List<WriteSheetData> getList(char prefix) {
        List<WriteSheetData> list = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            WriteSheetData d = new WriteSheetData();
            d.setString(prefix + "-" + j);
            list.add(d);
        }
        return list;
    }
}
