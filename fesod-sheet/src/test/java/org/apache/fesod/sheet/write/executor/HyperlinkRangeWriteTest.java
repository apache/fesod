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

package org.apache.fesod.sheet.write.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.metadata.data.HyperlinkData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Verifies that {@link HyperlinkData} coordinates reach the written file.
 *
 * <p>A single hyperlink can then cover a range of cells instead of collapsing onto the cell that
 * carries the data.
 */
@Tag(Tags.WRITE)
class HyperlinkRangeWriteTest extends AbstractExcelTest {

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void hyperlink_covers_the_requested_range(ExcelFormat format) throws IOException {
        File file = createTempFile("hyperlink-range-", format);
        write(file, format, link(0, 0, 2, 2));

        assertRange(readFirstHyperlink(file), 0, 0, 2, 2);
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void hyperlink_without_coordinates_stays_on_its_cell(ExcelFormat format) throws IOException {
        File file = createTempFile("hyperlink-cell-", format);
        write(file, format, baseLink());

        assertRange(readFirstHyperlink(file), 1, 1, 1, 1);
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void hyperlink_covers_a_range_away_from_origin(ExcelFormat format) throws IOException {
        File file = createTempFile("hyperlink-offset-", format);
        write(file, format, link(1, 2, 3, 4));

        assertRange(readFirstHyperlink(file), 1, 2, 3, 4);
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void hyperlink_uses_relative_coordinates(ExcelFormat format) throws IOException {
        File file = createTempFile("hyperlink-relative-", format);
        HyperlinkData link = baseLink();
        link.setRelativeFirstRowIndex(1);
        link.setRelativeFirstColumnIndex(1);
        link.setRelativeLastRowIndex(1);
        link.setRelativeLastColumnIndex(1);
        write(file, format, link);

        // The data cell is B2 (1, 1), so a +1 relative offset lands on C3.
        assertRange(readFirstHyperlink(file), 2, 2, 2, 2);
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void hyperlink_falls_back_when_the_absolute_index_is_negative(ExcelFormat format) throws IOException {
        File file = createTempFile("hyperlink-negative-", format);
        write(file, format, link(-1, -1, -1, -1));

        // A negative index is not a valid cell, so the data cell B2 is used instead.
        assertRange(readFirstHyperlink(file), 1, 1, 1, 1);
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void two_hyperlinks_keep_their_own_ranges(ExcelFormat format) throws IOException {
        File file = createTempFile("hyperlink-two-", format);
        WriteCellData<String> first = new WriteCellData<>("first");
        first.setHyperlinkData(link(0, 0, 0, 0));

        WriteCellData<String> second = new WriteCellData<>("second");
        second.setHyperlinkData(link(2, 2, 3, 3));

        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(first, "B1", "C1"));
        rows.add(Arrays.asList("A2", "B2", "C2"));
        rows.add(Arrays.asList("A3", "B3", second));
        FesodSheet.write(file).excelType(format.toExcelTypeEnum()).sheet().doWrite(rows);

        List<Hyperlink> written = readHyperlinks(file);
        Assertions.assertEquals(2, written.size(), "expected two hyperlinks");
        Hyperlink byOrigin = written.stream()
                .filter(link -> link.getFirstRow() == 0)
                .findFirst()
                .orElse(null);
        Hyperlink byOffset = written.stream()
                .filter(link -> link.getFirstRow() == 2)
                .findFirst()
                .orElse(null);
        assertRange(byOrigin, 0, 0, 0, 0);
        assertRange(byOffset, 2, 2, 3, 3);
    }

    private static HyperlinkData link(int firstRow, int firstColumn, int lastRow, int lastColumn) {
        HyperlinkData link = baseLink();
        link.setFirstRowIndex(firstRow);
        link.setFirstColumnIndex(firstColumn);
        link.setLastRowIndex(lastRow);
        link.setLastColumnIndex(lastColumn);
        return link;
    }

    private static HyperlinkData baseLink() {
        HyperlinkData link = new HyperlinkData();
        link.setAddress("https://fesod.apache.org/");
        link.setHyperlinkType(HyperlinkData.HyperlinkType.URL);
        return link;
    }

    private static void write(File file, ExcelFormat format, HyperlinkData link) {
        WriteCellData<String> anchorCell = new WriteCellData<>("link");
        anchorCell.setHyperlinkData(link);

        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList("A1", "B1", "C1"));
        rows.add(Arrays.asList("A2", anchorCell, "C2"));
        rows.add(Arrays.asList("A3", "B3", "C3"));
        ExcelTypeEnum type = format.toExcelTypeEnum();
        FesodSheet.write(file).excelType(type).sheet().doWrite(rows);
    }

    private static void assertRange(Hyperlink link, int firstRow, int firstColumn, int lastRow, int lastColumn) {
        Assertions.assertNotNull(link, "no hyperlink was written to the file");
        Assertions.assertAll(
                () -> Assertions.assertEquals(firstRow, link.getFirstRow(), "firstRow"),
                () -> Assertions.assertEquals(firstColumn, link.getFirstColumn(), "firstColumn"),
                () -> Assertions.assertEquals(lastRow, link.getLastRow(), "lastRow"),
                () -> Assertions.assertEquals(lastColumn, link.getLastColumn(), "lastColumn"));
    }

    private static Hyperlink readFirstHyperlink(File file) throws IOException {
        List<Hyperlink> links = readHyperlinks(file);
        return links.isEmpty() ? null : links.get(0);
    }

    private static List<Hyperlink> readHyperlinks(File file) throws IOException {
        List<Hyperlink> links = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 0; rowIndex < 5; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                for (int columnIndex = 0; columnIndex < 6; columnIndex++) {
                    Cell cell = row.getCell(columnIndex);
                    if (cell == null) {
                        continue;
                    }
                    Hyperlink link = cell.getHyperlink();
                    // A link spanning several cells is returned for each of them, so dedupe by identity.
                    if (link != null && links.stream().noneMatch(existing -> existing == link)) {
                        links.add(link);
                    }
                }
            }
        }
        return links;
    }
}
