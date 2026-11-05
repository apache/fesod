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
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.metadata.csv.CsvSheet;
import org.apache.fesod.sheet.metadata.csv.CsvWorkbook;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Tag(Tags.FORMAT)
@Tag(Tags.WRITE)
class CsvCellValueReplacementTest extends AbstractExcelTest {

    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2024, 1, 15, 12, 30, 45);

    @ParameterizedTest(name = "{0}")
    @MethodSource("dateSetters")
    void number_replaces_date(String dateType, Consumer<Cell> setDate) throws IOException {
        List<String> lines = writeCsv(cell -> {
            setDate.accept(cell);
            cell.setCellValue(42);
        });

        Assertions.assertEquals(Collections.singletonList("42"), lines);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dateSetters")
    void number_replaces_date_after_blank(String dateType, Consumer<Cell> setDate) throws IOException {
        List<String> lines = writeCsv(cell -> {
            setDate.accept(cell);
            cell.setBlank();
            cell.setCellValue(42);
        });

        Assertions.assertEquals(Collections.singletonList("42"), lines);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dateSetters")
    void number_replacement_preserves_number_format(String dateType, Consumer<Cell> setDate) throws IOException {
        List<String> lines = writeCsv(cell -> {
            Workbook workbook = cell.getSheet().getWorkbook();
            CellStyle style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
            cell.setCellStyle(style);
            setDate.accept(cell);
            cell.setCellValue(42);
        });

        Assertions.assertEquals(Collections.singletonList("42.00"), lines);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dateSetters")
    void date_replaces_number(String dateType, Consumer<Cell> setDate) throws IOException {
        List<String> lines = writeCsv(cell -> {
            cell.setCellValue(42);
            setDate.accept(cell);
        });

        Assertions.assertEquals(Collections.singletonList("2024-01-15 12:30:45"), lines);
    }

    @Test
    void write_handler_can_replace_date_with_number() throws IOException {
        File file = createTempFile(ExcelFormat.CSV);
        FesodSheet.write(file)
                .withBom(false)
                .needHead(false)
                .registerWriteHandler(new CellWriteHandler() {
                    @Override
                    public void afterCellDispose(CellWriteHandlerContext context) {
                        context.getCell().setCellValue(DATE_TIME);
                        context.getCell().setCellValue(42);
                    }
                })
                .csv()
                .doWrite(Collections.singletonList(Collections.singletonList(0)));

        Assertions.assertEquals(
                Collections.singletonList("42"), Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
    }

    private static Stream<Arguments> dateSetters() {
        Date date = Date.from(DATE_TIME.atZone(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return Stream.of(
                Arguments.of("LocalDateTime", (Consumer<Cell>) cell -> cell.setCellValue(DATE_TIME)),
                Arguments.of("Date", (Consumer<Cell>) cell -> cell.setCellValue(date)),
                Arguments.of("Calendar", (Consumer<Cell>) cell -> cell.setCellValue(calendar)));
    }

    private List<String> writeCsv(Consumer<Cell> setValue) throws IOException {
        File file = createTempFile(ExcelFormat.CSV);
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            CsvWorkbook workbook = new CsvWorkbook(writer, Locale.US, false, false, StandardCharsets.UTF_8, false);
            try (CsvSheet sheet = (CsvSheet) workbook.createSheet()) {
                setValue.accept(sheet.createRow(0).createCell(0));
            }
        }
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }
}
