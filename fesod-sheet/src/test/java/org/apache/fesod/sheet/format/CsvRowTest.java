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

package org.apache.fesod.sheet.format;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.fesod.sheet.FastExcel;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.exception.ExcelWriteDataConvertException;
import org.apache.fesod.sheet.metadata.csv.CsvCell;
import org.apache.fesod.sheet.metadata.csv.CsvRow;
import org.apache.fesod.sheet.metadata.csv.CsvSheet;
import org.apache.fesod.sheet.metadata.csv.CsvWorkbook;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag(Tags.FORMAT)
@ExtendWith(MockitoExtension.class)
public class CsvRowTest {

    @Mock
    private CsvWorkbook csvWorkbook;

    @Mock
    private CsvSheet csvSheet;

    private CsvRow csvRow;

    @TempDir
    File tempDir;

    private File fileCsvNoModel;
    private File fileCsvModel;

    @BeforeEach
    void setUp() {
        csvRow = new CsvRow(csvWorkbook, csvSheet, 1);

        Cell firstCell = csvRow.createCell(0, CellType.STRING);
        firstCell.setCellValue("No");
        Cell middleCell = csvRow.createCell(1, CellType.STRING);
        middleCell.setCellValue("Name");
        Cell lastCell = csvRow.createCell(2, CellType.STRING);
        lastCell.setCellValue("Age");

        fileCsvNoModel = new File(tempDir, "csv-no-model.csv");
        fileCsvModel = new File(tempDir, "csv-model.csv");
    }

    @Test
    void testGetCellWithFirstIndexShouldReturnFirstCell() {
        Cell actualCell = csvRow.getCell(0);
        Assertions.assertNotNull(actualCell);
        Assertions.assertEquals("No", actualCell.getStringCellValue());
    }

    @Test
    void testGetCellWithMiddleIndexShouldReturnMiddleCell() {
        Cell actualCell = csvRow.getCell(1);
        Assertions.assertNotNull(actualCell);
        Assertions.assertEquals("Name", actualCell.getStringCellValue());
    }

    @Test
    void testGetCellWithLastIndexShouldReturnLastCell() {
        Cell actualCell = csvRow.getCell(2);
        Assertions.assertNotNull(actualCell);
        Assertions.assertEquals("Age", actualCell.getStringCellValue());
    }

    @Test
    void testGetCellWithOutOfBoundsIndexShouldReturnNull() {
        Cell actualCell1 = csvRow.getCell(3);
        Assertions.assertNull(actualCell1);

        Cell actualCell2 = csvRow.getCell(-1);
        Assertions.assertNull(actualCell2);
    }

    /**
     * POI {@link Row#getLastCellNum()} semantics: {@code -1} for a row without
     * any cell.
     */
    @Test
    void testGetLastCellNumWithEmptyRowShouldReturnMinusOne() {
        CsvRow row = new CsvRow(csvWorkbook, csvSheet, 0);
        Assertions.assertEquals(-1, row.getLastCellNum());
    }

    /**
     * POI {@link Row#getLastCellNum()} semantics: index of the last cell plus
     * one. For a dense row with cells at columns 0..2 this equals the cell
     * count (3).
     */
    @Test
    void testGetLastCellNumWithDenseRowShouldReturnLastColumnPlusOne() {
        Assertions.assertEquals(3, csvRow.getLastCellNum());
    }

    /**
     * POI {@link Row#getLastCellNum()} semantics for sparse rows: cells only
     * at columns 0 and 5 must yield 6 (index of the last cell plus one), not
     * the physical cell count (2).
     * <p>
     * The canonical POI iteration {@code for (int j = 0; j < row.getLastCellNum(); j++)}
     * relies on this: before the fix the loop stopped at j = 2 and never reached column 5.
     */
    @Test
    void testGetLastCellNumWithSparseRowShouldReachEveryColumn() {
        CsvRow row = new CsvRow(csvWorkbook, csvSheet, 0);
        Cell cell0 = row.createCell(0, CellType.STRING);
        cell0.setCellValue("ZERO");
        Cell cell5 = row.createCell(5, CellType.STRING);
        cell5.setCellValue("FIVE");

        Assertions.assertEquals(6, row.getLastCellNum());

        // Canonical POI iteration must visit the column-5 cell
        Cell visitedColumn5 = null;
        for (int j = 0; j < row.getLastCellNum(); j++) {
            Cell cell = row.getCell(j);
            if (j == 5) {
                visitedColumn5 = cell;
            }
        }
        Assertions.assertNotNull(visitedColumn5, "iteration must reach the cell at column 5");
        Assertions.assertEquals("FIVE", visitedColumn5.getStringCellValue());
    }

    /**
     * POI {@link Row} semantics: cells are addressed by column index, not by
     * their position in the internal storage. Creating cells out of order must
     * still allow each one to be retrieved from its own column index, and
     * absent columns must return {@code null}.
     */
    @Test
    void testGetCellWithOutOfOrderCreationShouldReturnCellByColumnIndex() {
        CsvRow row = new CsvRow(csvWorkbook, csvSheet, 0);
        Cell cell5 = row.createCell(5, CellType.STRING);
        cell5.setCellValue("FIVE");
        Cell cell0 = row.createCell(0, CellType.STRING);
        cell0.setCellValue("ZERO");
        Cell cell3 = row.createCell(3, CellType.STRING);
        cell3.setCellValue("THREE");

        Assertions.assertEquals(0, row.getCell(0).getColumnIndex());
        Assertions.assertEquals("ZERO", row.getCell(0).getStringCellValue());
        Assertions.assertEquals(3, row.getCell(3).getColumnIndex());
        Assertions.assertEquals("THREE", row.getCell(3).getStringCellValue());
        Assertions.assertEquals(5, row.getCell(5).getColumnIndex());
        Assertions.assertEquals("FIVE", row.getCell(5).getStringCellValue());

        // Absent columns must not return a neighbor cell
        Assertions.assertNull(row.getCell(1));
        Assertions.assertNull(row.getCell(2));
        Assertions.assertNull(row.getCell(4));
        Assertions.assertNull(row.getCell(6));
        Assertions.assertEquals(3, row.getPhysicalNumberOfCells());
    }

    /**
     * POI {@link Row#createCell(int)} must replace an existing cell at the same
     * column instead of appending a second physical cell.
     */
    @Test
    void testCreateCellTwiceOnSameColumnShouldReplaceExistingCell() {
        CsvRow row = new CsvRow(csvWorkbook, csvSheet, 0);
        Cell first = row.createCell(5, CellType.STRING);
        first.setCellValue("first");
        Cell second = row.createCell(5, CellType.STRING);
        second.setCellValue("second");

        Assertions.assertEquals(1, row.getPhysicalNumberOfCells());
        Assertions.assertSame(second, row.getCell(5));
        Assertions.assertEquals(5, row.getCell(5).getColumnIndex());
        Assertions.assertEquals("second", row.getCell(5).getStringCellValue());
    }

    /**
     * POI {@link Row#removeCell(Cell)} semantics: removing one cell must leave
     * the remaining cells addressable under their original column indexes.
     */
    @Test
    void testRemoveCellWithMiddleCellShouldKeepRemainingCellsAddressable() {
        Cell middleCell = csvRow.getCell(1);
        csvRow.removeCell(middleCell);

        Assertions.assertEquals(2, csvRow.getPhysicalNumberOfCells());
        Assertions.assertEquals(0, csvRow.getCell(0).getColumnIndex());
        Assertions.assertEquals("No", csvRow.getCell(0).getStringCellValue());
        Assertions.assertNull(csvRow.getCell(1));
        Assertions.assertEquals(2, csvRow.getCell(2).getColumnIndex());
        Assertions.assertEquals("Age", csvRow.getCell(2).getStringCellValue());
    }

    /**
     * A row handler that masks a column by re-creating its cell must replace the cell, as it does on xlsx.
     */
    @Test
    void csvWrite_rowHandlerRecreatingCellShouldReplaceIt() throws Exception {
        File csvFile = new File(tempDir, "csv-recreate-cell.csv");
        FastExcel.write(csvFile)
                .head(head())
                .registerWriteHandler(new RowWriteHandler() {
                    @Override
                    public void afterRowDispose(RowWriteHandlerContext context) {
                        if (!Boolean.TRUE.equals(context.getHead())) {
                            context.getRow().createCell(1).setCellValue("***");
                        }
                    }
                })
                .csv()
                .doWrite(data());

        List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
        Assertions.assertEquals(Arrays.asList("1,***,20", "2,***,21", "3,***,20"), lines.subList(1, lines.size()));
    }

    /**
     * With {@code @ExcelProperty(index)} gaps the row is sparse, so a row handler must find cells by column index.
     */
    @Test
    void csvWrite_rowHandlerOnSparseRowShouldAddressCellsByColumnIndex() throws Exception {
        File csvFile = new File(tempDir, "csv-sparse-row.csv");
        FastExcel.write(csvFile, IndexGapData.class)
                .registerWriteHandler(new RowWriteHandler() {
                    @Override
                    public void afterRowDispose(RowWriteHandlerContext context) {
                        if (!Boolean.TRUE.equals(context.getHead())) {
                            Row row = context.getRow();
                            Cell last = row.getCell(3);
                            row.createCell(2)
                                    .setCellValue(last == null ? "missing" : "before " + last.getStringCellValue());
                        }
                    }
                })
                .csv()
                .doWrite(Collections.singletonList(new IndexGapData("a", "b", "d")));

        List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
        Assertions.assertEquals(2, lines.size());
        Assertions.assertEquals("a,b,before d,d", lines.get(1));
    }

    /**
     * Real-file integration test: verifies that out-of-order cell creation,
     * replacement of an existing column, and sparse rows are flushed as a
     * single CSV record with exactly one field per column.
     * <p>
     * Without the fix, {@code createCell} appends without replacing and
     * {@code CsvSheet#flushData} receives cells in non-ascending order, which
     * corrupts the output (extra fields / shifted values).
     */
    @Test
    void csvWrite_withOutOfOrderAndReplacedCells_producesCorrectFile() throws Exception {
        File csvFile = new File(tempDir, "out-of-order-test.csv");

        try (java.io.Writer writer = Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8)) {
            CsvWorkbook workbook = new CsvWorkbook(writer, null, false, false, StandardCharsets.UTF_8, false);
            CsvSheet sheet = (CsvSheet) workbook.createSheet();
            CsvRow row = (CsvRow) sheet.createRow(0);

            Cell cell5 = row.createCell(5, CellType.STRING);
            cell5.setCellValue("FIVE");
            Cell cell0 = row.createCell(0, CellType.STRING);
            cell0.setCellValue("ZERO");
            // Re-create column 5: must replace, not append a second physical cell
            Cell cell5Replaced = row.createCell(5, CellType.STRING);
            cell5Replaced.setCellValue("FIVE-REPLACED");
            Cell cell2 = row.createCell(2, CellType.STRING);
            cell2.setCellValue("TWO");

            sheet.close();
        }

        List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
        Assertions.assertEquals(1, lines.size());
        // Columns: 0=ZERO, 1=empty, 2=TWO, 3=empty, 4=empty, 5=FIVE-REPLACED
        Assertions.assertEquals("ZERO,,TWO,,,FIVE-REPLACED", lines.get(0));
    }

    @Test
    void testCsvWriteWithOutModelShouldSuccess() {
        FastExcel.write(fileCsvNoModel)
                .head(head())
                .registerWriteHandler(new AssertCsvHeadDataWriteHandler(head(), data()))
                .csv()
                .doWrite(data());
    }

    @Test
    void testCsvWriteWithModelShouldSuccess() {
        FastExcel.write(fileCsvModel)
                .head(SimpleCsvData.class)
                .registerWriteHandler(new AssertCsvHeadDataWriteHandler(head(), data()))
                .csv()
                .doWrite(modelData());
    }

    /**
     * Verifies that {@link CsvCell} handles {@link java.sql.Date} the same way as
     * {@link org.apache.fesod.sheet.metadata.data.WriteCellData}: the date is extracted
     * via {@code toLocalDate().atStartOfDay()}, stripping any time component that may
     * exist in the underlying milliseconds (common when JDBC drivers create
     * {@code java.sql.Date} from a {@code java.util.Date} with time info).
     */
    @Test
    void testCsvCellSqlDateConversion() {
        // Create a java.sql.Date from a java.util.Date that has a time component
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JUNE, 15, 23, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);
        java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());

        Cell cell = csvRow.createCell(0, CellType.NUMERIC);
        cell.setCellValue(sqlDate);

        LocalDateTime dateValue = ((CsvCell) cell).getLocalDateTimeCellValue();
        // java.sql.Date is date-only: derive expected value from sqlDate itself to avoid timezone sensitivity
        Assertions.assertEquals(sqlDate.toLocalDate().atStartOfDay(), dateValue);
    }

    /**
     * Verifies that {@link CsvCell} handles {@link java.sql.Time} the same way as
     * {@link org.apache.fesod.sheet.metadata.data.WriteCellData}: the time is extracted
     * via {@code toLocalTime().atDate(DateUtils.EPOCH)}, stripping any date
     * component that may exist in the underlying milliseconds.
     */
    @Test
    void testCsvCellSqlTimeConversion() {
        // Create a java.sql.Time from a java.util.Date that has a date component
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JUNE, 15, 12, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);
        java.sql.Time sqlTime = new java.sql.Time(cal.getTimeInMillis());

        Cell cell = csvRow.createCell(0, CellType.NUMERIC);
        cell.setCellValue(sqlTime);

        LocalDateTime dateValue = ((CsvCell) cell).getLocalDateTimeCellValue();
        // java.sql.Time is time-only: derive expected value from sqlTime itself to avoid timezone sensitivity
        Assertions.assertEquals(sqlTime.toLocalTime().atDate(DateUtils.EPOCH), dateValue);
    }

    /**
     * Real-file integration test: writes a physical CSV file containing
     * {@code java.sql.Date} and {@code java.sql.Time} values via the
     * {@link CsvCell} API, then reads the file back to verify the output.
     * <p>
     * Without the fix, {@code CsvCell.setCellValueImpl(Date)} calls
     * {@code value.toInstant()} which throws {@code UnsupportedOperationException}
     * on Java 9+ for {@code java.sql.Date}/{@code java.sql.Time}.
     */
    @Test
    void csvWrite_withSqlDateAndTime_producesCorrectFile() throws Exception {
        File csvFile = new File(tempDir, "sql-date-test.csv");

        try (java.io.Writer writer = Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8)) {
            CsvWorkbook workbook = new CsvWorkbook(writer, null, false, false, StandardCharsets.UTF_8, false);
            CsvSheet sheet = (CsvSheet) workbook.createSheet();
            CsvRow row = (CsvRow) sheet.createRow(0);

            // java.sql.Date — without fix: UnsupportedOperationException
            Cell dateCell = row.createCell(0, CellType.NUMERIC);
            dateCell.setCellValue(java.sql.Date.valueOf("2024-01-15"));

            // java.sql.Time — without fix: UnsupportedOperationException
            Cell timeCell = row.createCell(1, CellType.NUMERIC);
            timeCell.setCellValue(java.sql.Time.valueOf("12:30:45"));

            sheet.close();
        }

        // Read file back and verify date/time strings
        List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
        Assertions.assertEquals(1, lines.size());
        String line = lines.get(0);
        Assertions.assertTrue(line.contains("2024-01-15"), "CSV should contain date 2024-01-15, got: " + line);
        Assertions.assertTrue(line.contains("12:30:45"), "CSV should contain time 12:30:45, got: " + line);
    }

    /**
     * Real-file integration test: writes a physical CSV file containing a
     * {@link Calendar} value via the {@link CsvCell} API, then reads the file
     * back to verify the output.
     * <p>
     * Without the fix, {@link CsvCell#setCellValueImpl(Calendar)} sets the cell
     * to {@code NUMERIC} but does not mark it as a date
     * ({@code numericCellType = NumericCellTypeEnum.DATE}), so
     * {@link CsvSheet} takes the number branch in {@code buildCellValue},
     * finds {@code numberValue} null, and writes an empty field - the
     * Calendar value is silently lost. The sibling {@code Date} and
     * {@code LocalDateTime} setters already set the date type.
     */
    @Test
    void csvWrite_withCalendar_producesCorrectFile() throws Exception {
        File csvFile = new File(tempDir, "calendar-test.csv");

        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 12, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);

        try (java.io.Writer writer = Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8)) {
            CsvWorkbook workbook = new CsvWorkbook(writer, null, false, false, StandardCharsets.UTF_8, false);
            CsvSheet sheet = (CsvSheet) workbook.createSheet();
            CsvRow row = (CsvRow) sheet.createRow(0);

            // Calendar - without fix: written as an empty field (silent data loss)
            Cell cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(cal);

            sheet.close();
        }

        List<String> lines = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
        Assertions.assertEquals(1, lines.size());
        String line = lines.get(0);
        Assertions.assertTrue(
                line.contains("2024-01-15"), "CSV should contain the calendar date 2024-01-15, got: " + line);
    }

    @Test
    void csvWrite_convertFailureExceptionShouldBeHashable() {
        File csvFile = new File(tempDir, "csv-convert-failure.csv");
        List<OptionalCsvData> data = Collections.singletonList(new OptionalCsvData("1", Optional.of("abc")));

        // The context held by the exception references the CSV workbook, sheet, row and cell. JUnit hashes
        // exceptions when it collects nested throwables, so a recursive hashCode hides the real error.
        ExcelWriteDataConvertException e = Assertions.assertThrows(
                ExcelWriteDataConvertException.class,
                () -> FastExcel.write(csvFile, OptionalCsvData.class).csv().doWrite(data));
        Assertions.assertDoesNotThrow(e::hashCode);
    }

    @Test
    void csvWrite_cellsAndRowsCollectedInHashSetsShouldStayDistinct() {
        File csvFile = new File(tempDir, "csv-hash-set.csv");
        List<Cell> cellList = new ArrayList<>();
        Set<Cell> cellSet = new HashSet<>();
        Set<Row> rowSet = new HashSet<>();
        FastExcel.write(csvFile)
                .head(head())
                .registerWriteHandler(new CellWriteHandler() {
                    @Override
                    public void afterCellDispose(CellWriteHandlerContext context) {
                        cellList.add(context.getCell());
                        cellSet.add(context.getCell());
                    }
                })
                .registerWriteHandler(new RowWriteHandler() {
                    @Override
                    public void afterRowCreate(RowWriteHandlerContext context) {
                        rowSet.add(context.getRow());
                    }
                })
                .csv()
                .doWrite(Arrays.asList(Arrays.asList("1", "Jackson", "20"), Arrays.asList("1", "Jackson", "20")));

        // a header row and two identical data rows, each cell and row a distinct object
        Assertions.assertEquals(9, cellList.size());
        Assertions.assertEquals(9, cellSet.size());
        Assertions.assertEquals(3, rowSet.size());
        for (Cell cell : cellList) {
            Assertions.assertTrue(cellSet.contains(cell));
            Assertions.assertTrue(rowSet.contains(cell.getRow()));
        }
    }

    @Getter
    @AllArgsConstructor
    public static class OptionalCsvData {
        @ExcelProperty("No")
        private String no;

        @ExcelProperty("Description")
        private Optional<String> description;
    }

    private static List<SimpleCsvData> modelData() {
        List<SimpleCsvData> data = new ArrayList<>();
        data.add(new SimpleCsvData("1", "Jackson", "20"));
        data.add(new SimpleCsvData("2", "Tom", "21"));
        data.add(new SimpleCsvData("3", "Sophia", "20"));
        return data;
    }

    private static List<List<String>> data() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("1", "Jackson", "20"));
        data.add(Arrays.asList("2", "Tom", "21"));
        data.add(Arrays.asList("3", "Sophia", "20"));
        return data;
    }

    private List<List<String>> head() {
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList("No"));
        head.add(Arrays.asList("Name"));
        head.add(Arrays.asList("Age"));
        return head;
    }

    @Getter
    @AllArgsConstructor
    public static class IndexGapData {
        @ExcelProperty(value = "A", index = 0)
        private String a;

        @ExcelProperty(value = "B", index = 1)
        private String b;

        @ExcelProperty(value = "D", index = 3)
        private String d;
    }
}
