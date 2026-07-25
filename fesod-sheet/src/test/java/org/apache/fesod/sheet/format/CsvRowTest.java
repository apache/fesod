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
import java.util.List;
import org.apache.fesod.sheet.FastExcel;
import org.apache.fesod.sheet.metadata.csv.CsvCell;
import org.apache.fesod.sheet.metadata.csv.CsvRow;
import org.apache.fesod.sheet.metadata.csv.CsvSheet;
import org.apache.fesod.sheet.metadata.csv.CsvWorkbook;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
}
